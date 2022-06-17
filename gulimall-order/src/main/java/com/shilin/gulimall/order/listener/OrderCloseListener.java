package com.shilin.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.shilin.gulimall.order.entity.OrderEntity;
import com.shilin.gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-08 11:41:58
 */
@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listen(Message message, Channel channel, OrderEntity orderEntity) throws IOException {
        System.out.println("收到下订单消息" + orderEntity.getOrderSn());
        try {
            orderService.closeOrder(orderEntity);
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicReject(deliveryTag, true);
        }

    }
}
