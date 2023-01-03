package com.rkyang.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 采购完成对象
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/3
 */
@Data
public class PurchaseDoneVO {

    /**
     * 采购单id
     */
    @NotNull
    private Long id;

    /**
     * 采购项信息
     */
    private List<PurchaseDoneItemVO> items;
}
