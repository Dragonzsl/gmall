package com.shilin.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.shilin.common.constant.AuthConstant;
import com.shilin.common.exception.CodeEnum;
import com.shilin.common.utils.R;
import com.shilin.common.vo.MemberResVo;
import com.shilin.gulimall.auth.feign.MemberFeignService;
import com.shilin.gulimall.auth.feign.ThirdPartyFeignService;
import com.shilin.gulimall.auth.vo.UserLoginVo;
import com.shilin.gulimall.auth.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-17 10:51:34
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("mobile") String mobile) {
        String codeStr = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + mobile);
        if (codeStr != null) {
            long s = Long.parseLong(codeStr.split("_")[1]);
            if (System.currentTimeMillis() - s < 60000) {
                return R.error(CodeEnum.SMS_CODE_EXCEPTION.getCode(), CodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 4);
        String codeCache = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + mobile, codeCache, 15, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(mobile, code);
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo,
                           BindingResult result,
                           RedirectAttributes redirectAttributes){
        if (result.hasErrors()){

            Map<String, String> errors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, fieldError -> {
                if (fieldError.getDefaultMessage() != null) {
                    return fieldError.getDefaultMessage();
                } else {
                    return "";
                }
            }));
//            model.addAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //验证码
        String code = userRegisterVo.getCode();
        String s = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhoneNum());
        if (!StringUtils.isEmpty(s)){
            if (code.equals(s.split("_")[0])){
                //删除验证码
                redisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + userRegisterVo.getPhoneNum());

                R r = memberFeignService.register(userRegisterVo);
                if (r.getCode() == 0){
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    Map<String ,String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }else {
                Map<String ,String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }

        }else {
            Map<String ,String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            RedirectAttributes attribute = redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
//        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession httpSession){
        R r = memberFeignService.login(userLoginVo);
        if (r.getCode() == 0){
            MemberResVo data = r.getData("data", new TypeReference<MemberResVo>() {
            });
            httpSession.setAttribute(AuthConstant.LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        }else {
            Map<String ,String > errors = new HashMap<>();
            errors.put("errors", r.getData("msg", new TypeReference<String >(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession httpSession){
        Object attribute = httpSession.getAttribute(AuthConstant.LOGIN_USER);
        if (attribute == null){
            //未登录
            return "login";
        }{
            //已登陆
            return "redirect:http://gulimall.com";
        }

    }
}
