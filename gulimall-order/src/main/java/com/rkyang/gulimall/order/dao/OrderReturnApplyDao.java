package com.rkyang.gulimall.order.dao;

import com.rkyang.gulimall.order.entity.OrderReturnApplyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单退货申请
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:37:49
 */
@Mapper
public interface OrderReturnApplyDao extends BaseMapper<OrderReturnApplyEntity> {
	
}
