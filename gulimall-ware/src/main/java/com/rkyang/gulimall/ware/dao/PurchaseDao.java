package com.rkyang.gulimall.ware.dao;

import com.rkyang.gulimall.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:41:21
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
