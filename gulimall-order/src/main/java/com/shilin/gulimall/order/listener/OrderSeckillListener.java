package com.shilin.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.shilin.common.to.mq.SeckillOrderTo;
import com.shilin.gulimall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-18 12:06:58
 */
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listen(Message message, Channel channel, SeckillOrderTo seckillOrderTo) throws IOException {
        try {
            orderService.createSeckillOrder(seckillOrderTo);
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicReject(deliveryTag, true);
        }

    }
}
