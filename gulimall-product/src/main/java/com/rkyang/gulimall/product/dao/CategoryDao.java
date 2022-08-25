package com.rkyang.gulimall.product.dao;

import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
