package com.shilin.gulimall.order.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shilin.common.exception.NoStockException;
import com.shilin.common.to.SkuHasStockVo;
import com.shilin.common.to.mq.SeckillOrderTo;
import com.shilin.common.utils.PageUtils;
import com.shilin.common.utils.Query;
import com.shilin.common.utils.R;
import com.shilin.common.vo.MemberResVo;
import com.shilin.gulimall.order.constant.OrderConstant;
import com.shilin.gulimall.order.dao.OrderDao;
import com.shilin.gulimall.order.entity.OrderEntity;
import com.shilin.gulimall.order.entity.OrderItemEntity;
import com.shilin.gulimall.order.entity.PaymentInfoEntity;
import com.shilin.gulimall.order.enume.OrderStatusEnum;
import com.shilin.gulimall.order.feign.CartFeignService;
import com.shilin.gulimall.order.feign.MemberFeignService;
import com.shilin.gulimall.order.feign.ProductFeignService;
import com.shilin.gulimall.order.feign.WareFeignService;
import com.shilin.gulimall.order.interceptor.LoginUserInterceptor;
import com.shilin.gulimall.order.service.OrderItemService;
import com.shilin.gulimall.order.service.OrderService;
import com.shilin.gulimall.order.service.PaymentInfoService;
import com.shilin.gulimall.order.to.OrderCreateTo;
import com.shilin.gulimall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    private final ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 确认订单
     *
     * @return OrderConfirmVo
     * @throws ExecutionException   ExecutionException
     * @throws InterruptedException InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResVo memberResVo = LoginUserInterceptor.loginUser.get();
        //拿到当前线程的请求信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //共享给其他线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //远程查询所有的收货地址列表
            List<MemberAddressVo> address = memberFeignService.getAddress(memberResVo.getId());
            confirmVo.setAddress(address);
        }, threadPoolExecutor);


        CompletableFuture<Void> getCurrentUserCartItemsFuture = CompletableFuture.runAsync(() -> {
            //共享给其他线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //查询当前用户的购物车项
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, threadPoolExecutor).thenRunAsync(() -> {
            //远程查询商品库存信息
            List<Long> collect = confirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R hasStock = wareFeignService.getSkuHasStock(collect);
            List<SkuHasStockVo> data = hasStock.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if (data != null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        }, threadPoolExecutor);


        //查询积分信息
        Integer integration = memberResVo.getIntegration();
        confirmVo.setIntegration(integration);

        //防重令牌
        String token = UUID.randomUUID(true).toString(true);
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResVo.getId(),
                token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddressFuture,getCurrentUserCartItemsFuture).get();
        return confirmVo;
    }

    /**
     * 提交订单
     *
     * @param orderSubmitVo OrderSubmitVo
     * @return SubmitOrderResponseVo
     */
//    @GlobalTransactional 不适用于高并发
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
        MemberResVo memberResVo = LoginUserInterceptor.loginUser.get();
        //redis lua脚本
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //执行脚本
        //返回值：1-成功 0-失败
        Long execute = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResVo.getId()),
                orderSubmitVo.getOrderToken());
        if (execute != null){
            if (execute == 0L){
                //验证失败
                submitOrderResponseVo.setCode(1);
                return submitOrderResponseVo;
            }else {
                //验证成功
                //创建订单等信息
                OrderCreateTo order = createOrder();
                //验价
                BigDecimal payPrice = order.getPayPrice();
                if (Math.abs(payPrice.subtract(orderSubmitVo.getPayPrice()).doubleValue()) < 0.01) {
                    //验价成功
                    //保存订单
                    saveOrder(order);
                    //库存锁定
                    WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                    wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                    List<OrderItemVo> collect = order.getOrderItems().stream().map(orderItemEntity -> {
                        OrderItemVo orderItemVo = new OrderItemVo();
                        orderItemVo.setSkuId(orderItemEntity.getSkuId());
                        orderItemVo.setCount(orderItemEntity.getSkuQuantity());
                        orderItemVo.setTitle(orderItemEntity.getSkuName());
                        return orderItemVo;
                    }).collect(Collectors.toList());
                    wareSkuLockVo.setLocks(collect);
                    R r = wareFeignService.orderLockStock(wareSkuLockVo);
                    if (r.getCode() == 0) {
                        //库存锁定成功
                        submitOrderResponseVo.setCode(0);
                        submitOrderResponseVo.setOrder(order.getOrder());
//                        int i = 1/0;
                        //将订单信息发给MQ
                        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                        return submitOrderResponseVo;
                    }else {
                        //库存锁定失败
                            throw new NoStockException("库存锁定失败");

                    }

                }else {
                    //验价失败
                    submitOrderResponseVo.setCode(2);
                    return submitOrderResponseVo;
                }
            }

        }

        return submitOrderResponseVo;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    /**
     * 关闭订单
     *
     * @param orderEntity 订单
     */
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        OrderEntity byId = this.getById(orderEntity.getId());
        if (byId.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            OrderEntity entity = new OrderEntity();
            entity.setId(orderEntity.getId());
            entity.setStatus(OrderStatusEnum.CANCLED.getCode());
            entity.setModifyTime(new Date());
            this.updateById(entity);
        }
        rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", byId);
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        BigDecimal bigDecimal = orderEntity.getTotalAmount().setScale(2, RoundingMode.UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setBody("");
        payVo.setSubject("结算");

        return payVo;
    }

    /**
     * 查询订单和订单项
     *
     * @param params params
     * @return PageUtils
     */
    @Override
    public PageUtils queryPageWithListAndItem(Map<String, Object> params) {
        MemberResVo memberResVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberResVo.getId())
        );
        List<OrderEntity> records = page.getRecords();
        List<OrderEntity> orderEntityList = records.stream().peek(orderEntity -> {
            List<OrderItemEntity> orderItemEntityList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderEntity.getOrderSn()));
            orderEntity.setOrderItemEntityList(orderItemEntityList);
        }).collect(Collectors.toList());

        page.setRecords(orderEntityList);
        return new PageUtils(page);
    }

    @Override
    public String handlePayResult(PayAsyncVo payAsyncVo) {
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setCallbackTime(payAsyncVo.getNotify_time());
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfoEntity.setTotalAmount(payAsyncVo.getTotal_amount());
        paymentInfoService.save(paymentInfoEntity);


        if ("TRADE_SUCCESS".equalsIgnoreCase(payAsyncVo.getTrade_status()) || "TRADE_FINISHED".equalsIgnoreCase(payAsyncVo.getTrade_status())){
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderSn(payAsyncVo.getOut_trade_no());
            orderEntity.setStatus(OrderStatusEnum.PAYED.getCode());
            orderEntity.setModifyTime(new Date());
            this.update(orderEntity, new UpdateWrapper<OrderEntity>().eq("order_sn", payAsyncVo.getOut_trade_no()));
        }

        return null;
    }

    /**
     * 创建秒杀订单
     *
     * @param orderTo seckillOrderTo
     */
    @Override
    @Transactional
    public void createSeckillOrder(SeckillOrderTo orderTo) {
//        MemberResVo memberResponseVo = LoginUserInterceptor.loginUser.get();
//        MemberResVo memberResVo = LoginUserInterceptor.loginUser.get();
        //1. 创建订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
//        orderEntity.setMemberUsername(memberResVo.getUsername());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setCreateTime(new Date());
        orderEntity.setPayAmount(orderTo.getSeckillPrice().multiply(new BigDecimal(orderTo.getNum())));
        this.save(orderEntity);
        //2. 创建订单项
        R r = productFeignService.spuInfoBySkuId(orderTo.getSkuId());
        if (r.getCode() == 0) {
            SeckillSkuInfoVo skuInfo = r.getData(new TypeReference<SeckillSkuInfoVo>() {
            });
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(orderTo.getOrderSn());
            orderItemEntity.setSpuId(skuInfo.getSpuId());
            orderItemEntity.setCategoryId(skuInfo.getCatalogId());
            orderItemEntity.setSkuId(skuInfo.getSkuId());
            orderItemEntity.setSkuName(skuInfo.getSkuName());
            orderItemEntity.setSkuPic(skuInfo.getSkuDefaultImg());
            orderItemEntity.setSkuPrice(skuInfo.getPrice());
            orderItemEntity.setSkuQuantity(orderTo.getNum());
            orderItemService.save(orderItemEntity);
        }

    }

    /**
     * 保存订单
     *
     * @param order OrderCreateTo
     */
    private void saveOrder(OrderCreateTo order) {
        //保存订单
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        //保存订单项列表
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 所有订单相关数据
     *
     * @return OrderCreateTo
     */
    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //构建订单
        OrderEntity orderEntity = buildOrder();
        orderCreateTo.setOrder(orderEntity);
        //构建订单项列表
        List<OrderItemEntity> orderItemEntityList = buildOrderItems(orderEntity.getOrderSn());
        orderCreateTo.setOrderItems(orderItemEntityList);
        //运费
        orderCreateTo.setFare(orderEntity.getFreightAmount());
        //应付价格
        orderCreateTo.setPayPrice(orderEntity.getPayAmount());

        return orderCreateTo;
    }

    /**
     * 创建订单项列表
     *
     * @param orderSn 订单号
     * @return List<OrderItemEntity>
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
        if (items != null && items.size() > 0) {
            return items.stream().map(orderItemVo -> {
                OrderItemEntity orderItemEntity = buildOrderItem(orderItemVo);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 创建订单项
     *
     * @param orderItemVo OrderItemVo
     * @return OrderItemEntity
     */
    private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        /*sku信息*/
        //商品编号
        orderItemEntity.setSkuId(orderItemVo.getSkuId());
        //商品编号
        orderItemEntity.setSkuName(orderItemVo.getTitle());
        //商品图片
        orderItemEntity.setSkuPic(orderItemVo.getImage());
        //商品价格
        orderItemEntity.setSkuPrice(orderItemVo.getPrice());
        //商品数量
        orderItemEntity.setSkuQuantity(orderItemVo.getCount());
        //商品销售属性组合
        String skuAttr = StringUtils.collectionToDelimitedString(orderItemVo.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        //赠送积分
        orderItemEntity.setGiftIntegration(orderItemVo.getPrice().multiply(BigDecimal.valueOf(orderItemVo.getCount())).intValue());
        //赠送成长值
        orderItemEntity.setGiftGrowth(orderItemVo.getPrice().multiply(BigDecimal.valueOf(orderItemVo.getCount())).intValue());

        /*spu信息*/
        R r = productFeignService.spuInfoBySkuId(orderItemVo.getSkuId());
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuName(data.getSpuName());
//        orderItemEntity.setSpuPic();
        orderItemEntity.setSpuBrand(String.valueOf(data.getBrandId()));
        orderItemEntity.setCategoryId(data.getCatalogId());

        /*优惠及价格信息*/
        //商品促销分解金额
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        //优惠券优惠分解金额
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        //积分优惠分解金额
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        //该商品经过优惠后的分解金额
        BigDecimal multiply = orderItemEntity.getSkuPrice()
                .multiply(BigDecimal.valueOf(orderItemEntity.getSkuQuantity()))
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(multiply);

        return orderItemEntity;
    }

    /**
     * 创建订单实体类
     *
     * @return OrderEntity
     */
    private OrderEntity buildOrder() {
        //生成订单号
        Snowflake snowflake = IdUtil.getSnowflake(1L, 1L);
        String s = snowflake.nextIdStr();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(s);
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        R fare = wareFeignService.getFare(orderSubmitVo.getAddrId());
        MemberReceiveAddressEntity data = fare.getData(new TypeReference<MemberReceiveAddressEntity>() {
        });

        MemberResVo memberResVo = LoginUserInterceptor.loginUser.get();
        //member_id
        orderEntity.setMemberId(memberResVo.getId());
        //用户名
        orderEntity.setMemberUsername(memberResVo.getUsername());
        //邮递费用
        String phone = data.getPhone();
        String substring = phone.substring(phone.length() - 1);
        orderEntity.setFreightAmount(new BigDecimal(substring));

        /*收货人信息*/
        //收货人姓名
        orderEntity.setReceiverName(data.getName());
        //收货人电话
        orderEntity.setReceiverPhone(data.getPhone());
        //收货人邮编
        orderEntity.setReceiverPostCode(data.getPostCode());
        //省份
        orderEntity.setReceiverProvince(data.getProvince());
        //城市
        orderEntity.setReceiverCity(data.getCity());
        //区
        orderEntity.setReceiverRegion(data.getRegion());
        //详细地址
        orderEntity.setReceiverDetailAddress(data.getDetailAddress());

        /*计算价格及优惠*/
        List<OrderItemEntity> orderItemEntityList = buildOrderItems(orderEntity.getOrderSn());
        BigDecimal promotionAmount = new BigDecimal("0.0");
        BigDecimal integrationAmount = new BigDecimal("0.0");
        BigDecimal couponAmount = new BigDecimal("0.0");
        BigDecimal payMount = new BigDecimal("0.0");
        int giftGrowth = 0;
        int giftIntegration = 0;
        if (orderItemEntityList != null && orderItemEntityList.size() > 0) {
            for (OrderItemEntity orderItemEntity : orderItemEntityList) {
                //促销优化金额（促销价、满减、阶梯价）
                promotionAmount = promotionAmount.add(orderItemEntity.getPromotionAmount());
                //积分抵扣金额
                integrationAmount = integrationAmount.add(orderItemEntity.getIntegrationAmount());
                //优惠券抵扣金额
                couponAmount = couponAmount.add(orderItemEntity.getCouponAmount());
                //应付总额
                payMount = payMount.add(orderItemEntity.getRealAmount());
                //成长值
                giftGrowth = giftGrowth + orderItemEntity.getGiftGrowth();
                //积分
                giftIntegration = giftIntegration + orderItemEntity.getGiftIntegration();
            }
        }
        //促销优化金额（促销价、满减、阶梯价）
        orderEntity.setPromotionAmount(promotionAmount);
        //积分抵扣金额
        orderEntity.setIntegrationAmount(integrationAmount);
        //优惠券抵扣金额
        orderEntity.setCouponAmount(couponAmount);
        //订单总额
        orderEntity.setTotalAmount(payMount);
        //应付总额
        BigDecimal subtract = payMount.subtract(promotionAmount).subtract(integrationAmount).subtract(couponAmount).add(orderEntity.getFreightAmount());
        orderEntity.setPayAmount(subtract);

        //订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //自动确认时间（天）
        orderEntity.setAutoConfirmDay(7);
        //可以获得的积分
        orderEntity.setIntegration(giftIntegration);
        //可以获得的成长值
        orderEntity.setGrowth(giftGrowth);
        //确认收货状态[0->未确认；1->已确认]
        orderEntity.setConfirmStatus(0);
        //删除状态【0->未删除；1->已删除】
        orderEntity.setDeleteStatus(0);
        //创建时间
        orderEntity.setCreateTime(new Date());

        return orderEntity;
    }

}
