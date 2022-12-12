package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.constant.product.AttrEnum;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.rkyang.gulimall.product.dao.AttrDao;
import com.rkyang.gulimall.product.dao.AttrGroupDao;
import com.rkyang.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.rkyang.gulimall.product.entity.AttrEntity;
import com.rkyang.gulimall.product.entity.AttrGroupEntity;
import com.rkyang.gulimall.product.service.AttrGroupService;
import com.rkyang.gulimall.product.service.AttrService;
import com.rkyang.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.rkyang.gulimall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrgroupRelationDao;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private AttrService attrService;

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
        if (params.get("key") != null && StringUtils.isNotBlank(params.get("key").toString())) {
            wrapper.and((obj) -> obj.eq("attr_group_id", params.get("key")).or().like("attr_group_name", params.get("key")));
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {

        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        if (!CollectionUtils.isEmpty(attrgroupRelationEntities)) {
            List<Long> attrIds = attrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            return attrDao.selectBatchIds(attrIds);
        }

        return null;
    }

    @Override
    public PageUtils getAttrNoRelation(Map<String, Object> params, Long attrGroupId) {
        // 获取分组的分类id
        AttrGroupEntity attrGroupEntity = this.baseMapper.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 获取分类下的所有分组id
        List<AttrGroupEntity> attrGroupEntities = this.baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIdS = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 获取分组关联的所有规格参数的attrID
        List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIdS));
        List<Long> attrIdS = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        // 获取未被关联的规格参数
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().
                eq("catelog_id", catelogId).
                eq("attr_type", AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIdS)) {
            wrapper.notIn("attr_id", attrIdS);
        }
        // 封装检索条件
        Object key = params.get("key");
        if (key != null) {
            wrapper.and(o -> o.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = attrDao.selectPage(new Query<AttrEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void deleteRelation(AttrVO[] attrVOS) {
        attrgroupRelationDao.deleteRelation(Arrays.asList(attrVOS));
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(long catelogId) {
        // 查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 查询所有属性
        List<AttrGroupWithAttrsVO> collect = attrGroupEntities.stream().map(o -> {
            AttrGroupWithAttrsVO vo = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(o, vo);
            List<AttrEntity> attrRelation = getAttrRelation(vo.getAttrGroupId());
            vo.setAttrs(attrRelation);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }
}