package com.rkyang.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品上架，es传输模型
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
@Data
public class SkuEsTO {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasCount;
    private Long hotScore;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
