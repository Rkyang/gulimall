package com.rkyang.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

