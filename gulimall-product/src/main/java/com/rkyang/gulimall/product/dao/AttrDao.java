package com.rkyang.gulimall.product.dao;

import com.rkyang.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 14:56:01
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
