package com.rkyang.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.to.SkuReductionTO;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:14:12
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTO reductionTO);
}

