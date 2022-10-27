package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.BrandDao;
import com.rkyang.gulimall.product.dao.CategoryBrandRelationDao;
import com.rkyang.gulimall.product.entity.BrandEntity;
import com.rkyang.gulimall.product.entity.CategoryBrandRelationEntity;
import com.rkyang.gulimall.product.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        String key = params.get("key").toString();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(obj -> obj.eq("brand_id", key)).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 修改品牌表信息，并修改相关表中冗余的品牌名称
     */
    @Override
    @Transactional
    public void updateAndRelation(BrandEntity brand) {
        updateById(brand);
        if (StringUtils.isNotBlank(brand.getName())) {
            CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
            entity.setBrandName(brand.getName());
            categoryBrandRelationDao.update(entity, new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brand.getBrandId()));
        }
    }
}