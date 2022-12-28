package com.rkyang.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠、满减远程调用数据传输对象
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/12/28
 */
@Data
public class SkuReductionTO {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
