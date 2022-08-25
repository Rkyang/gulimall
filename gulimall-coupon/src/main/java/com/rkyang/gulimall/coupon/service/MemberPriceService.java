package com.rkyang.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:14:12
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

