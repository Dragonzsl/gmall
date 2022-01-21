package com.shilin.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.shilin.common.utils.R;
import com.shilin.gulimall.cart.feign.ProductFeignService;
import com.shilin.gulimall.cart.interceptor.CartInterceptor;
import com.shilin.gulimall.cart.service.CartService;
import com.shilin.gulimall.cart.to.SkuInfoTo;
import com.shilin.gulimall.cart.to.UserInfoTo;
import com.shilin.gulimall.cart.vo.Cart;
import com.shilin.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-25 17:30:03
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String  o = (String) cartOps.get(skuId.toString());

        if (StringUtils.isEmpty(o)){
            //没有添加过当前商品
            //获取商品信息
            R skuInfo = productFeignService.getSkuInfo(skuId);
            CartItem cartItem = new CartItem();
            SkuInfoTo skuInfoData = skuInfo.getData("skuInfo", new TypeReference<SkuInfoTo>() {
            });

            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                cartItem.setCheck(true);
                cartItem.setCount(1);
                cartItem.setImage(skuInfoData.getSkuDefaultImg());
                cartItem.setPrice(skuInfoData.getPrice());
                cartItem.setSkuId(skuId);
                cartItem.setTitle(skuInfoData.getSkuTitle());
            }, threadPoolExecutor);

            CompletableFuture<Void> getSkuSaleAttrTask = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttr = productFeignService.getSkuSaleAttr(skuId);
                cartItem.setSkuAttr(skuSaleAttr);
            }, threadPoolExecutor);

            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrTask).get();
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }else {
            //已经添加过当前商品
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }

    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String  o = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(o, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfoTo userInfoTo = CartInterceptor.userInfoToThreadLocal.get();
        Long userId = userInfoTo.getUserId();
        Cart cart = new Cart();
        if (userId != null){
            //用户已登陆
            String CART_PREFIX = "gulimall:cart:";
            //先将临时购物车数据合并到已登录的购物车数据中
            List<CartItem> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            if (tempCartItems != null && tempCartItems.size() > 0){
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
            }
            clearCart(CART_PREFIX + userInfoTo.getUserKey());
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        }else {
            //用户未登录
            String CART_PREFIX = "gulimall:cart:";
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        }
        return cart;
    }

    /**
     * 清空购物车
     *
     * @param cartKey key
     */
    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void changeChecked(Long skuId, Integer checked) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(checked == 1);
        String s = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void changeCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void deleteItem(Long deleteId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(deleteId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.userInfoToThreadLocal.get();
        String CART_PREFIX = "gulimall:cart:";
        String cartKey = CART_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
        List<CartItem> collect = null;
        if (cartItems != null && cartItems.size() > 0) {
            collect = cartItems.stream()
                    .filter(CartItem::getCheck)
                    .map(cartItem -> {
                        BigDecimal skuPrice = productFeignService.getSkuPrice(cartItem.getSkuId());
                        cartItem.setPrice(skuPrice);
                        return cartItem;
                    })
                    .collect(Collectors.toList());
        }

        return collect;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> boundHashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = boundHashOps.values();
        if (values != null && values.size() > 0){
            return values.stream().map(o -> JSON.parseObject(o.toString(), CartItem.class)).collect(Collectors.toList());
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.userInfoToThreadLocal.get();
        String cartKey = "";
        String CART_PREFIX = "gulimall:cart:";
        if (userInfoTo.getUserId()!=null){
            //用户已登录
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else {
            //用户未登录
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(cartKey);
    }
}
