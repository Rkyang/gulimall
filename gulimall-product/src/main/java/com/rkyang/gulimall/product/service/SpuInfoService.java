package com.rkyang.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.product.entity.SpuInfoEntity;
import com.rkyang.gulimall.product.vo.spusave.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO spuInfo);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

