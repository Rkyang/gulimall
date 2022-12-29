package com.rkyang.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.SkuInfoDao;
import com.rkyang.gulimall.product.entity.SkuInfoEntity;
import com.rkyang.gulimall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(qw -> qw.like("sku_id", key).or().like("sku_name", key));
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) > 0) {
            queryWrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }
}