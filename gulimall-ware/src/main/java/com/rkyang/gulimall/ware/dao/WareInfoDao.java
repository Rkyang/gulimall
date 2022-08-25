package com.rkyang.gulimall.ware.dao;

import com.rkyang.gulimall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:41:21
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
