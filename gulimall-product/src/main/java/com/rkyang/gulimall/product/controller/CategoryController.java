package com.rkyang.gulimall.product.controller;

import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品三级分类
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 15:25:51
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有商品分类，以及分类下的所有子分类，封装成树形结构
     */
    @RequestMapping("/list/tree")
    public R listWithTree(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        return R.ok().put("listTree", categoryEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
//		categoryService.updateById(category);
		categoryService.updateAndRelation(category);
        return R.ok();
    }

    /**
     * 拖拽修改排序
     */
    @RequestMapping("/update/sort")
    //@RequiresPermissions("product:category:update")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        // TODO 删除前判断分类是否被引用
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
