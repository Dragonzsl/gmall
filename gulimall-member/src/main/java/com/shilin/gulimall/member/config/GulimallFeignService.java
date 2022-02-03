package com.shilin.gulimall.member.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-30 11:19:18
 */
@Configuration
public class GulimallFeignService {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return template -> {
            //使用RequestContextHolder拿到老请求的请求数据
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                //拿到请求头
                HttpServletRequest request = requestAttributes.getRequest();
                String cookie = request.getHeader("Cookie");
                template.header("Cookie", cookie);
            }
        };
    }
}
