package com.rkyang.gulimall.member.dao;

import com.rkyang.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:27:24
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
