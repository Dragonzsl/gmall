package com.shilin.gulimall.auth.feign;

import com.shilin.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-17 17:31:14
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code);
}
