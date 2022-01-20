package com.shilin.gulimall.auth.feign;

import com.shilin.common.utils.R;
import com.shilin.gulimall.auth.vo.SocialUser;
import com.shilin.gulimall.auth.vo.UserLoginVo;
import com.shilin.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-20 11:26:11
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo userLoginVo);

    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;
}
