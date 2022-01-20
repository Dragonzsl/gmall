package com.shilin.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-21 16:45:10
 */
@Data
public class SpuBoundsTo implements Serializable {
    private static final long serialVersionUID = 3746322324855730979L;
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
