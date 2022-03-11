package com.shilin.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用RabbitMQ
 * 		1、引入amqp场景，RabbitAutoConfiguration自动生效
 * 		2、容器中自动配置：CachingConnectionFactory、RabbitTemplate、AmqpAdmin、RabbitMessagingTemplate
 * 		3、	@ConfigurationProperties(prefix = "spring.rabbitmq")
 * 			public class RabbitProperties
 * 		4、开启@EnableRabbit
 *
 * 	本地事务失效问题
 * 		同一个对象内事务方法互调默认失效，原因：绕过了代理对象，事务使用代理对象来控制
 * 	解决：使用代理对象来调用事务方法
 * 		1）、spring-boot-starter-aop；引入aspectj
 * 		2）、@EnableAspectJAutoProxy(exposeProxy = true)；开启 aspectj 动态代理功能。
 * 		以后所有的动态代理都是 aspectj创建的（即使没有接口也可以创建动态代理）
 * 		对外暴露代理对象
 * 		3）、本类互相调用代理对象
 * 			OrderServiceImpl orderServiceImpl = (OrderServiceImpl) AopContext.currentProxy();
 * 			orderServiceImpl.b();
 * 			orderServiceImpl.c();
 *
 * 	seata控制分布式事务
 * 	1）、每一个微服务先必须创建 undo_log；
 * 	2）、安装事务协调器；seata-server: https://github.com/seata/seata/releases
 * 	3）、整合
 * 		1、导入依赖 spring-cloud-starter-alibaba-seata
 * 		2、解压并启动 seata-server registry.conf: 注册中心配置；修改 registry type=nacos file.conf
 * 		3、所有想要用到分布式事务的微服务使用 seata DataSourceProxy 代理自己的数据源
 * 		4、每个微服务都必须导入 registry.conf 	file.conf :	vgroup_mapping.{application.name}-fescar-service-group="default"
 * 		5、启动测试分布式事务
 * 		6、给分布式大事务的入口标注 @GloablTransactional
 * 		7、每一个远程的小事务用 @Transactional
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@EnableRedisHttpSession
@EnableFeignClients
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
