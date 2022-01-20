package com.shilin.gulimall.auth.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.shilin.common.exception.CodeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-20 16:44:30
 */
@Configuration
public class AuthSentinelWebConfig {

    @Bean
    public BlockExceptionHandler sentinelBlockExceptionHandler(){

        return (request, response, e) -> {
            response.setStatus(CodeEnum.TOO_MANY_REQUESTS.getCode());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json");
            PrintWriter writer = response.getWriter();
            writer.println(CodeEnum.TOO_MANY_REQUESTS.getMsg());
            writer.flush();
            writer.close();
        };
    }
}
