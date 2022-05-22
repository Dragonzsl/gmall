package com.shilin.gulimall.order.feign;

import com.shilin.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-29 18:05:29
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/getAddress")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
