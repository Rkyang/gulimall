package com.rkyang.gulimall.member.dao;

import com.rkyang.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:27:24
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
