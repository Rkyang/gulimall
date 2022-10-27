package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.AttrGroupDao;
import com.rkyang.gulimall.product.entity.AttrGroupEntity;
import com.rkyang.gulimall.product.service.AttrGroupService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (cateId != 0) {
            wrapper.eq("catelog_id", cateId);
        }
        if (StringUtils.isNotBlank(params.get("key").toString())) {
            wrapper.and((obj) -> obj.eq("attr_group_id", params.get("key")).or().like("attr_group_name", params.get("key")));
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}