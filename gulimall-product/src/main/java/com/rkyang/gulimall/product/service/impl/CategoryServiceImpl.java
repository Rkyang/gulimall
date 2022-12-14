package com.rkyang.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.CategoryBrandRelationDao;
import com.rkyang.gulimall.product.dao.CategoryDao;
import com.rkyang.gulimall.product.entity.CategoryBrandRelationEntity;
import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.CategoryService;
import com.rkyang.gulimall.product.vo.Catalog2VO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 获取所有商品分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        // 获取子级分类，封装树形结构
        // 获取一级分类
        return categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                // 获取所有子级分类
                .peek(category -> category.setChild(getChild(category, categoryEntities)))
                // 排序
                .sorted((category1, category2) -> (category1.getSort() == null ? 0 : category1.getSort()) - (category2 == null ? 0 : category2.getSort()))
                // 生成新的集合
                .collect(Collectors.toList());
    }

    /**
     * 递归获取所有子级商品分类
     * @param parent 需要获取子级分类的分类对象
     * @param all 所有分类对象（在该集合中获取所有子级分类信息）
     * @return 所有子级分类对象
     */
    private List<CategoryEntity> getChild(CategoryEntity parent, List<CategoryEntity> all) {
        // 获取父级分类为parent对象的分类信息
        return all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(parent.getCatId()))
                // 递归获取所有子级分类
                .peek(category -> category.setChild(getChild(category, all)))
                // 排序
                .sorted((category1, category2) -> (category1.getSort() == null ? 0 : category1.getSort()) - (category2 == null ? 0 : category2.getSort()))
                // 生成新的集合
                .collect(Collectors.toList());
    }

    @Override
    public Long[] findFullPath(Long catelogId) {
        CategoryEntity category = this.getById(catelogId);
        List<Long> fullPath = getAllParent(category, new ArrayList<>());
        Collections.reverse(fullPath);
        return fullPath.toArray(new Long[fullPath.size()]);
    }

    /**
     * 递归查询分类的所有上级id
     */
    private List<Long> getAllParent(CategoryEntity category, List<Long> path) {
        path.add(category.getCatId());
        if (category.getParentCid() != 0) {
            CategoryEntity parent = this.getById(category.getParentCid());
            this.getAllParent(parent, path);
        }
        return path;
    }

    /**
     * 修改分类信息，并修改相关的表中冗余的分类名称
     */
    @Override
    @Transactional
    public void updateAndRelation(CategoryEntity category) {
        updateById(category);
        if (StringUtils.isNotBlank(category.getName())) {
            CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
            entity.setCatelogName(category.getName());
            categoryBrandRelationDao.update(entity, new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", category.getCatId()));
        }
    }

    @Override
    public List<CategoryEntity> selectLevel1() {
        return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalog2And3() {
        Map<String, List<Catalog2VO>> result = new HashMap<>();

        List<CategoryEntity> allCatalog = this.list();

        List<CategoryEntity> level1 = allCatalog.stream().filter(catalog -> catalog.getCatLevel() == 1).collect(Collectors.toList());

        for (CategoryEntity l1 : level1) {
            List<CategoryEntity> child2 = this.getChild(l1, allCatalog);
            List<Catalog2VO> l2VOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(child2)) {
                l2VOS = child2.stream().map(l2 -> {
                    Catalog2VO l2VO = new Catalog2VO(l1.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> child3 = this.getChild(l2, allCatalog);
                    if (CollectionUtils.isNotEmpty(child3)) {
                        List<Catalog2VO.Catalog3VO> l3VOS = child3.stream().map(l3 -> new Catalog2VO.Catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName())).collect(Collectors.toList());
                        l2VO.setCatalog3List(l3VOS);
                    }
                    return l2VO;
                }).collect(Collectors.toList());
            }
            result.put(l1.getCatId().toString(), l2VOS);
        }
        return result;
    }
}