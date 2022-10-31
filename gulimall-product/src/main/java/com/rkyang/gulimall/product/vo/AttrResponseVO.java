package com.rkyang.gulimall.product.vo;

import com.rkyang.gulimall.product.entity.AttrEntity;
import lombok.Data;

/**
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/10/31
 */
@Data
public class AttrResponseVO extends AttrEntity {

    private String groupName;
    private String catelogName;
}
