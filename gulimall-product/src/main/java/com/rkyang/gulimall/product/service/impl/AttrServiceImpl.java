package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.rkyang.gulimall.product.dao.AttrDao;
import com.rkyang.gulimall.product.dao.AttrGroupDao;
import com.rkyang.gulimall.product.dao.CategoryDao;
import com.rkyang.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.rkyang.gulimall.product.entity.AttrEntity;
import com.rkyang.gulimall.product.entity.AttrGroupEntity;
import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.AttrService;
import com.rkyang.gulimall.product.vo.AttrResponseVO;
import com.rkyang.gulimall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrgroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        if (catId != 0) {
            queryWrapper.eq("attr_id", catId);
        }
        Object key = params.get("key");
        if (key != null && StringUtils.isNotBlank(key.toString())) {
            queryWrapper.and(item -> item.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        // 查询所属分类和所属分组
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVO> vos = records.stream().map((o) -> {
            AttrResponseVO attrResponseVO = new AttrResponseVO();
            BeanUtils.copyProperties(o, attrResponseVO);
            AttrAttrgroupRelationEntity attrgroupRelationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", o.getAttrId()));
            if (attrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrResponseVO.setGroupName(attrGroupEntity.getAttrGroupName());
                    CategoryEntity category = categoryDao.selectById(attrGroupEntity.getCatelogId());
                    if (category != null) {
                        attrResponseVO.setCatelogName(category.getName());
                    }
                }
            }
            return attrResponseVO;
        }).collect(Collectors.toList());
        pageUtils.setList(vos);
        return pageUtils;
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