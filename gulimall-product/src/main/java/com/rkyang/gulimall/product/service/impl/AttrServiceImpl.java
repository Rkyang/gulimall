package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.rkyang.gulimall.product.dao.AttrDao;
import com.rkyang.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.rkyang.gulimall.product.entity.AttrEntity;
import com.rkyang.gulimall.product.service.AttrService;
import com.rkyang.gulimall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrgroupRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public R saveAttr(AttrVO attr) {
        // 保存商品属性（规格参数）
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        // 保存商品属性和属性分组关联关系
        AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
        attrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrgroupRelationDao.insert(attrgroupRelationEntity);
        return R.ok();
    }
}