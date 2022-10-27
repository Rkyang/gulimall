package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.CategoryBrandRelationDao;
import com.rkyang.gulimall.product.entity.BrandEntity;
import com.rkyang.gulimall.product.entity.CategoryBrandRelationEntity;
import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.BrandService;
import com.rkyang.gulimall.product.service.CategoryBrandRelationService;
import com.rkyang.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean save(CategoryBrandRelationEntity entity) {
        // 查询到品牌和分类的name封入关联对象中
        BrandEntity brand = brandService.getById(entity.getBrandId());
        CategoryEntity category = categoryService.getById(entity.getCatelogId());
        entity.setBrandName(brand.getName());
        entity.setCatelogName(category.getName());
        return super.save(entity);
    }
}