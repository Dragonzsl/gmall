package com.shilin.gulimall.member.feign;

import com.shilin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-11 16:35:09
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    @RequestMapping("/order/order/listAndItem")
    R listAndItem(@RequestBody Map<String, Object> params);
}
