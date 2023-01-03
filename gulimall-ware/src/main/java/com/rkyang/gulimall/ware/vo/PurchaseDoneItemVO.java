package com.rkyang.gulimall.ware.vo;

import lombok.Data;

/**
 * 采购完成的采购项对象
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/3
 */
@Data
public class PurchaseDoneItemVO {

    private Long itemId;
    private Integer status;
    private String reason;
}
