package com.shilin.common.to;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-29 12:23:28
 */
@Data
public class SkuHasStockVo implements Serializable {
    private static final long serialVersionUID = -5203219525126617662L;
    private Long skuId;
    private Boolean hasStock;
}
