package com.shilin.gulimall.order.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-11-28 12:03:59
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用json序列化机制，进行消息的发送
     *
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 1、spring.rabbitmq.publisher-confirm-type=correlated
     * 2、设置RabbitTemplate的ConfirmCallback
     *
     *      只要消息抵达Broker，ack=true
     *
     * 设置消息抵达队列回调
     *     1、 #开启发送消息抵达队列的确认
     *        spring.rabbitmq.publisher-returns=true
     *     2、 #只要抵达队列，以异步方式发送回调 --> publisher-returns confirm
     *        spring.rabbitmq.template.mandatory=true
     *     3、设置确认回调
     *         setReturnCallback
     *
     *  消费端确认（保证每个消息被正确消费，此时才可以 Broker 删除这个消息）
     *  spring.rabbitmq.listener.simple.acknowledge-mode=manual 手动签收
     *      1、默认是自动确认的，只要消息被接收到，客户端会自动确认，服务端就会移出这个消息
     *          问题：我们收到很多消息，自动回复给服务器 ack，只有一个消息处理成功。宕机了，发生消息丢失
     *          解决：手动ack，只要我们没有明确告诉 MQ 货物被签收，没有 ack，消息就一直是 Unacked 状态。
     *              即使 Consumer 宕机，消息也不会丢失，会重新变为 Ready 状态，下一次有新的 Consumer 连接进来就发送给他。
     *      2、如何签收
     *          channel.basicAck(deliveryTag, false);签收；如果业务处理完成应该签收
     *          channel.basicNack(deliveryTag, false, true);拒签；业务处理失败，拒签
     */
    @PostConstruct //在MyRabbitConfig对象创建完成之后执行
    public void initRabbitTemplate(){
        //correlationData: 当前消息的唯一关联数据（这个是消息的唯一ID）
        //ack：消息是否成功收到
        //cause：失败的原因
         rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
             System.out.println("*****CONFIRM*****");
             System.out.println("correlationData-->" +correlationData + "===ack-->" +ack + "===cause-->" +cause);
             System.out.println();
         });

         //设置消息抵达队列的确认回调
        //message：投递失败的消息的详细信息
        //replyCode：回复的状态码
        //replyText：回复的文本内容
        //exchange：当时这个消息发送给的交换机
        //routingKey：当时这个消息发送的路由键
        rabbitTemplate.setReturnCallback(((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("*****FAIL MESSAGE*****");
            System.out.println("message-->" + message + "===replyCode-->" + replyCode + "===replyText-->" + replyText + "===exchange-->" + exchange + "===routingKey-->" + routingKey);
            System.out.println();
        }));
    }
}
