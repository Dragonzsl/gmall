package com.shilin.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-18 11:43:18
 */
@Data
public class SeckillOrderTo {
    private String orderSn; //订单号
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    private Integer num; //购买数量

    private Long memberId; //会员id


}
