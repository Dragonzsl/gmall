package com.shilin.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-29 17:41:41
 */
public class OrderConfirmVo {
    //收货地址
    @Setter
    @Getter
    private List<MemberAddressVo> address;

    //所有选中的购物项
    @Setter
    @Getter
    private List<OrderItemVo> items;

    //优惠券信息
    @Setter
    @Getter
    private Integer integration;

    //商品库存信息
    @Setter
    @Getter
    Map<Long, Boolean> stocks;

    //防重令牌
    @Setter
    @Getter
    private String orderToken;

    public BigDecimal getTotal() {
        //订单总额
        BigDecimal total = new BigDecimal("0.00");
        if (items != null && items.size() > 0){
            for (OrderItemVo item : items) {
                total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getCount())));
            }
        }
        return total;
    }

    public BigDecimal getPayPrice() {
        //应付价格
        return getTotal();
    }

    public Integer getCount(){
        Integer count = 0;
        if (items != null && items.size() > 0){
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
}
