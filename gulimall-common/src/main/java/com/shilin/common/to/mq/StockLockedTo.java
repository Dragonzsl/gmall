package com.shilin.common.to.mq;

import lombok.Data;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-12-07 11:58:56
 */
@Data
public class StockLockedTo {

    /**
     * 工作单id
     */
    private Long id;
    /**
     * 工作单详情
     */
    private StockDetailTo detail;
}
