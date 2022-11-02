package com.rkyang.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.entity.AttrEntity;
import com.rkyang.gulimall.product.vo.AttrResponseVO;
import com.rkyang.gulimall.product.vo.AttrVO;

import java.util.Map;

/**
 * 商品属性
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R saveAttr(AttrVO attr);

    PageUtils queryPage(Map<String, Object> params, Long catId, Integer code);

    AttrResponseVO getAttrInfo(Long attrId);

    void updateById(AttrVO attr);
}

