package com.shilin.gulimall.order;

import com.shilin.gulimall.order.entity.OrderReturnApplyEntity;
import com.shilin.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class GulimallOrderApplicationTests {

	@Autowired
	AmqpAdmin amqpAdmin;

	@Autowired
	RabbitTemplate rabbitTemplate;

	/**
	 * 1、如何创建Exchange,Queue,Binding
	 * 		1）、使用AmqpAdmin进行创建
	 * 2、如何收发消息
	 */
	@Test
	void createExchange(){
		DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Exchange【{}】创建成功",directExchange.getName());
	}

	/**
	 * 创建队列
	 */
	@Test
	void createQueue(){
		//public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
		//			@Nullable Map<String, Object> arguments)
		Queue queue = new Queue("hello-java-queue", true, false, false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue【{}】创建成功",queue.getName());
	}

	@Test
	void createBinding(){
		//public Binding(
		// 	String destination,【目的地】
		// 	DestinationType destinationType, 【目的地类型】
		// 	String exchange, 【交换机】
		// 	String routingKey,【路由键】
		//	@Nullable Map<String, Object> arguments 【自定义参数】
		//	)
		Binding binding = new Binding("hello-java-queue",
				Binding.DestinationType.QUEUE,
				"hello-java-exchange",
				"hello.java",
				null);
		amqpAdmin.declareBinding(binding);
		log.info("Binding创建成功");
	}

	/**
	 * 发送消息
	 */
	@Test
	void sendMessage(){
		for (int i = 0;i < 10; i++) {
			OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
			orderReturnReasonEntity.setId(1L);
			orderReturnReasonEntity.setName("aaa");
			orderReturnReasonEntity.setSort(1);
			orderReturnReasonEntity.setStatus(2);
			orderReturnReasonEntity.setCreateTime(new Date());
			rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);
		}
	}

	@Test
	void contextLoads() {
	}

}
