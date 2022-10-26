package com.rkyang.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.Query;
import com.rkyang.gulimall.product.dao.CategoryDao;
import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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

    private List<Long> getAllParent(CategoryEntity category, List<Long> path) {
        path.add(category.getCatId());
        if (category.getParentCid() != 0) {
            CategoryEntity parent = this.getById(category.getParentCid());
            this.getAllParent(parent, path);
        }
        return path;
    }
}