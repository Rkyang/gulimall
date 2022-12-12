package com.rkyang.gulimall.product.vo;

import com.rkyang.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/11/29
 */
@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
