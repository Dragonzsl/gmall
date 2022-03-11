package com.shilin.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-16 16:54:27
 */
@Configuration
public class MyThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties){
        return new org.apache.tomcat.util.threads.ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(), properties.getUnit(),new LinkedBlockingQueue<>(100000),
                 Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
