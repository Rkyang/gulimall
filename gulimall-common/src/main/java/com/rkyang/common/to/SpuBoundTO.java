package com.rkyang.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 积分远程调用数据传输对象
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/12/28
 */
@Data
public class SpuBoundTO {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
