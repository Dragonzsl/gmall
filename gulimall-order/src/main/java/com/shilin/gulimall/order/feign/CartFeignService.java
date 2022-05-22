package com.shilin.gulimall.order.feign;

import com.shilin.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-29 20:08:21
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/currentCartItem")
    List<OrderItemVo> getCurrentUserCartItems();
}
