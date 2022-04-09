package com.shilin.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-01 12:51:07
 */
@Data
public class OrderSubmitVo {

    //收货地址
    private Long addrId;

    //支付方式
    private Integer payType;

    //防重令牌
    private String orderToken;

    //应付价格
    private BigDecimal payPrice;

    //订单备注
    private String note;

}
