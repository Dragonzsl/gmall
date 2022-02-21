package com.shilin.gulimall.member.feign;

import com.shilin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-09 11:08:57
 */
@FeignClient(value = "gulimall-coupon")
public interface CouponFeign {
    @RequestMapping("/coupon/coupon/info/{id}")
    public R info(@PathVariable("id") Long id);
}
