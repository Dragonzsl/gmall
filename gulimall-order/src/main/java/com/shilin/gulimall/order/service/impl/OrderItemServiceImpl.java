package com.shilin.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import com.shilin.common.utils.PageUtils;
import com.shilin.common.utils.Query;
import com.shilin.gulimall.order.dao.OrderItemDao;
import com.shilin.gulimall.order.entity.OrderEntity;
import com.shilin.gulimall.order.entity.OrderItemEntity;
import com.shilin.gulimall.order.entity.OrderReturnReasonEntity;
import com.shilin.gulimall.order.service.OrderItemService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Service("orderItemService")
@RabbitListener(queues = {"hello-java-queue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues: 声明需要监听的所有队列
     *
     * 场景：
     *      1）、订单服务启动多个：同一个消息只能由一个客户端收到
     *      2）、只有一个消息完全处理完，方法运行完才能接收到下一个消息
     *
     * @param message                 org.springframework.amqp.core.Message 原生消息详细信息，头+体
     * @param orderReturnReasonEntity com.shilin.gulimall.order.entity.OrderReturnReasonEntity 发送的消息的类型
     * @param channel                 com.rabbitmq.client.Channel 当前传输数据的通道
     */
    @RabbitHandler
    public void receiveMessage(Message message,OrderReturnReasonEntity orderReturnReasonEntity,Channel channel) {
        System.out.println("收到消息：" + orderReturnReasonEntity.toString());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (deliveryTag % 2 == 0) {
                channel.basicAck(deliveryTag, false);
                System.out.println("签收货物。。。" + deliveryTag);
            } else {
                System.out.println("未签收货物。。。" + deliveryTag);
            }
        } catch (IOException e) {
            //网络中断
            try {
                //拒绝签收
                channel.basicNack(deliveryTag, false, true);
//                channel.basicReject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @RabbitHandler
    public void receiveMessage1(Message message,OrderEntity orderEntity,Channel channel) {
        System.out.println("收到消息：" + orderEntity.toString());
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
