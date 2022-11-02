package com.rkyang.gulimall.product.vo;

import lombok.Data;

/**
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/10/31
 */
@Data
public class AttrResponseVO extends AttrVO {

    private String groupName;
    private String catelogName;

    private Long[] catelogPath;
}
