package com.shilin.gulimall.order.to;

import com.shilin.gulimall.order.entity.OrderEntity;
import com.shilin.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-01 13:44:21
 */
@Data
public class OrderCreateTo {
    //订单
    private OrderEntity order;
    //订单项
    private List<OrderItemEntity> orderItems;
    //应付价格
    private BigDecimal payPrice;
    //运费
    private BigDecimal fare;

}
