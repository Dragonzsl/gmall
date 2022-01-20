package com.shilin.common.to.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author shilin
 * @email g1335026358@gmail.com
 * @date 2020-10-25 12:24:04
 */
@Data
public class SkuESModel implements Serializable {
    private static final long serialVersionUID = -2925196458965213590L;
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;


    @Data
    public static class Attrs implements Serializable {
        private static final long serialVersionUID = 4709625791820704431L;
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
