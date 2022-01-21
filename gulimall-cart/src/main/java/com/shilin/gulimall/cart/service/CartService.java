package com.shilin.gulimall.cart.service;

import com.shilin.gulimall.cart.vo.Cart;
import com.shilin.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-25 17:29:41
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void clearCart(String cartKey);

    void changeChecked(Long skuId, Integer checked);

    void changeCount(Long skuId, Integer num);

    void deleteItem(Long deleteId);

    List<CartItem> getCurrentUserCartItems();
}
