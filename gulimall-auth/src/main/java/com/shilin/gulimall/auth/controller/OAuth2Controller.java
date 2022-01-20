package com.shilin.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.shilin.common.constant.AuthConstant;
import com.shilin.common.utils.HttpUtils;
import com.shilin.common.utils.R;
import com.shilin.gulimall.auth.feign.MemberFeignService;
import com.shilin.common.vo.MemberResVo;
import com.shilin.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-21 12:20:00
 */
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession httpSession) throws Exception {

        Map<String ,String > headersMap = new HashMap<>();
        Map<String ,String > querys = new HashMap<>();

        Map<String, String > paramMap = new HashMap<>();
        paramMap.put("client_id", "2314323808");
        paramMap.put("client_secret", "a6ac89fcebbecca4f74fc84789ea2bb2");
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        //https://api.weibo.com/oauth2/access_token
//        HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, querys, paramMap);
        HttpResponse httpResponse = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post",
                headersMap,
                querys,
                paramMap);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200){
            //成功
            HttpEntity entity = httpResponse.getEntity();
            String s = EntityUtils.toString(entity);
            SocialUser socialUser = JSON.parseObject(s, SocialUser.class);
            R r = memberFeignService.oauthLogin(socialUser);
            if (r.getCode() == 0){
                //成功
                MemberResVo data = r.getData("data", new TypeReference<MemberResVo>() {
                });
                System.out.println("用户信息：==》" + data);
                httpSession.setAttribute(AuthConstant.LOGIN_USER, data);
                return "redirect:http://gulimall.com";
            }else {
                //失败
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else {
            //失败
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
