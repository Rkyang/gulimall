package com.rkyang.gulimall.product.controller;

import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.rkyang.gulimall.product.entity.AttrEntity;
import com.rkyang.gulimall.product.entity.AttrGroupEntity;
import com.rkyang.gulimall.product.service.AttrAttrgroupRelationService;
import com.rkyang.gulimall.product.service.AttrGroupService;
import com.rkyang.gulimall.product.service.CategoryService;
import com.rkyang.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.rkyang.gulimall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 15:25:51
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationService attrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{cateId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("cateId") Long cateId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, cateId);

        return R.ok().put("page", page);
    }

    /**
     * 获取分类下所有的分组以及分组中的规格参数（属性）
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") long catelogId) {
        List<AttrGroupWithAttrsVO> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		// 获取分组属性的分类完整路径（所有父级的分类id）
        Long[] catelogIds = categoryService.findFullPath(attrGroup.getCatelogId());
        attrGroup.setCatelogIds(catelogIds);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 获取属性分组关联的规格参数
     */
    @GetMapping("{attrGroupId}/attr/relation")
    public R getAttrRelation(@PathVariable("attrGroupId") Long attrGroupId) {

        List<AttrEntity> result = attrGroupService.getAttrRelation(attrGroupId);

        return R.ok().put("data", result);
    }

    /**
     * 获取属性分组未关联的规格参数（也未被其它分组关联）
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R getAttrNoRelation(@PathVariable("attrGroupId") Long attrGroupId,
                               @RequestParam Map<String, Object> params) {

        PageUtils page = attrGroupService.getAttrNoRelation(params, attrGroupId);

        return R.ok().put("page", page);
    }

    /**
     * 属性分组关联规格参数保存
     */
    @PostMapping("/attr/relation")
    public R addAttrRelation(@RequestBody List<AttrAttrgroupRelationEntity> entities) {
        attrgroupRelationService.saveBatch(entities);
        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 属性分组和规格参数的关联批量删除
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrVO[] attrVOS) {

        attrGroupService.deleteRelation(attrVOS);

        return R.ok();
    }
}
