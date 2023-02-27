package com.shilin.gulimall.order.controller;

import cn.hutool.core.lang.UUID;
import com.shilin.gulimall.order.entity.OrderEntity;
import com.shilin.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-28 13:26:08
 */
@RestController
public class SendMsgController {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/sendMsg")
    public String sendM(@RequestParam(value = "num",defaultValue = "10") Integer num){
        for (int i = 0;i < num; i++) {
            if (i%2==0) {
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId((long) i);
                orderReturnReasonEntity.setName("aaa");
                orderReturnReasonEntity.setSort(1);
                orderReturnReasonEntity.setStatus(2);
                orderReturnReasonEntity.setCreateTime(new Date());
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello.java",
                        orderReturnReasonEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setId((long) i);
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello.java",
                        orderEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }
}
