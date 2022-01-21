package com.shilin.gulimall.cart.controller;

import com.shilin.gulimall.cart.service.CartService;
import com.shilin.gulimall.cart.vo.Cart;
import com.shilin.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-25 17:47:11
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        /*UserInfoTo userInfoTo = CartInterceptor.userInfoToThreadLocal.get();
        System.out.println(userInfoTo);*/
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String changeChecked(@RequestParam("skuId") Long skuId,
                                @RequestParam("checked") Integer checked){
        cartService.changeChecked(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String changeCount(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num){
        cartService.changeCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("deleteId") Long deleteId){
        cartService.deleteItem(deleteId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/currentCartItem")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getCurrentUserCartItems();
    }
}
