package com.shilin.gulimall.member.interceptor;

import com.shilin.common.constant.AuthConstant;
import com.shilin.common.vo.MemberResVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-29 13:39:43
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResVo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/order/order/orderStatus/**", requestURI);
        if (match){
            return true;
        }
        boolean match1 = new AntPathMatcher().match("/member/**", requestURI);
        if (match1){
            return true;
        }

        Object attribute = request.getSession().getAttribute(AuthConstant.LOGIN_USER);
        if (attribute != null){
            //已登陆
            loginUser.set((MemberResVo) attribute);
            return true;
        }else {
            //未登录
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }


    }
}
