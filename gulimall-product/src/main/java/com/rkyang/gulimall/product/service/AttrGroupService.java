package com.rkyang.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long cateId);
}

