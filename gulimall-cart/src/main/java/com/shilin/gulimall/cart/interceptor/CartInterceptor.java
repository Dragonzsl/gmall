package com.shilin.gulimall.cart.interceptor;

import com.shilin.common.constant.AuthConstant;
import com.shilin.common.constant.CartConstant;
import com.shilin.common.vo.MemberResVo;
import com.shilin.gulimall.cart.to.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-25 17:47:45
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> userInfoToThreadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberResVo memberResVo = (MemberResVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (memberResVo != null){
            //用户登录
            userInfoTo.setUserId(memberResVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果user-key为空
        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserKey(s);
        }

        //放行之前，将封装好的数据放入ThreadLocal
        userInfoToThreadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = userInfoToThreadLocal.get();
        boolean tempUser = userInfoTo.isTempUser();
        if (!tempUser){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_EXPIRY);
            response.addCookie(cookie);
        }
    }
}
