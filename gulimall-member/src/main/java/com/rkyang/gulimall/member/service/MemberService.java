package com.rkyang.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-25 10:27:24
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

