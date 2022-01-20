package com.shilin.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-17 11:42:51
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 视图映射
     *
     * @param registry registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
