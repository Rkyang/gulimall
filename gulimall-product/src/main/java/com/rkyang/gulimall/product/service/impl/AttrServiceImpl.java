package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.constant.ProductConstant;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.rkyang.gulimall.product.dao.AttrDao;
import com.rkyang.gulimall.product.dao.AttrGroupDao;
import com.rkyang.gulimall.product.dao.CategoryDao;
import com.rkyang.gulimall.product.entity.*;
import com.rkyang.gulimall.product.service.AttrService;
import com.rkyang.gulimall.product.service.CategoryService;
import com.rkyang.gulimall.product.service.ProductAttrValueService;
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

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId, Integer code) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", code);

        if (catId != 0) {
            queryWrapper.eq("catelog_id", catId);
        }
        Object key = params.get("key");
        if (key != null && StringUtils.isNotBlank(key.toString())) {
            queryWrapper.and(item -> item.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        // ?????????????????????????????????
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVO> vos = records.stream().map((o) -> {
            AttrResponseVO attrResponseVO = new AttrResponseVO();
            BeanUtils.copyProperties(o, attrResponseVO);
            // ??????????????????????????????
            if (code.equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
                AttrAttrgroupRelationEntity attrgroupRelationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", o.getAttrId()));
                if (attrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrResponseVO.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }
            CategoryEntity category = categoryDao.selectById(o.getCatelogId());
            if (category != null) {
                attrResponseVO.setCatelogName(category.getName());
            }
            return attrResponseVO;
        }).collect(Collectors.toList());
        pageUtils.setList(vos);
        return pageUtils;
    }

    @Override
    @Transactional
    public R saveAttr(AttrVO attr) {
        // ????????????????????????????????????
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        // ?????????????????????????????????????????????
        if (attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrgroupRelationDao.insert(attrgroupRelationEntity);
        }
        return R.ok();
    }

    @Override
    public AttrResponseVO getAttrInfo(Long attrId) {
        AttrResponseVO vo = new AttrResponseVO();
        // ??????attr??????
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, vo);
        // ???????????????????????????
        AttrAttrgroupRelationEntity attrgroupRelation = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if (attrgroupRelation != null) {
            vo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
        }
        Long[] fullPath = categoryService.findFullPath(attrEntity.getCatelogId());
        if (fullPath != null) {
            vo.setCatelogPath(fullPath);
        }
        return vo;
    }

    @Override
    @Transactional
    public void updateById(AttrVO attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        this.updateById(entity);
        if (attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            // ???????????????????????????????????????
            AttrAttrgroupRelationEntity attrgroupRelation = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (attrgroupRelation != null) {
                // ??????
                attrgroupRelation.setAttrGroupId(attr.getAttrGroupId());
                attrgroupRelationDao.updateById(attrgroupRelation);
            } else {
                //??????
                AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrgroupRelationEntity.setAttrId(entity.getAttrId());
                attrgroupRelationDao.insert(attrgroupRelationEntity);
            }
        }
    }

    @Override
    @Transactional
    public void updateAttrSpu(Long spuId, List<ProductAttrValueEntity> entities) {
        // ???????????????????????????????????????????????????
        attrValueService.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<ProductAttrValueEntity> collect = entities.stream().peek(item -> item.setSpuId(spuId)).collect(Collectors.toList());

        attrValueService.saveBatch(collect);
    }
}