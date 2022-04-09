package com.shilin.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-02 13:20:06
 */
@Data
public class WareSkuLockVo {
    private String  orderSn;//订单号
    private List<OrderItemVo> locks;//需要锁库存的商品

}
