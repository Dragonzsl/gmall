package com.shilin.gulimall.order.vo;

import com.shilin.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-01 13:18:04
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//0-成功
}
