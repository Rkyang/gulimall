package com.rkyang.gulimall.product.controller;

import com.rkyang.common.constant.product.AttrEnum;
import com.rkyang.common.utils.PageUtils;
import com.rkyang.common.utils.R;
import com.rkyang.gulimall.product.service.AttrService;
import com.rkyang.gulimall.product.vo.AttrResponseVO;
import com.rkyang.gulimall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 商品属性
 *
 * @author rkyang
 * @email rkyang@outlook.com
 * @date 2022-08-24 15:25:51
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取规格参数列表
     */
    @GetMapping("/base/list/{catId}")
    public R baseList(@RequestParam Map<String, Object> params, @PathVariable("catId") Long catId) {
        PageUtils page = attrService.queryPage(params, catId, AttrEnum.ATTR_TYPE_BASE.getCode());

        return R.ok().put("page", page);
    }

    /**
     * 获取销售属性列表
     */
    @GetMapping("/sale/list/{catId}")
    public R saleList(@RequestParam Map<String, Object> params, @PathVariable("catId") Long catId) {
        PageUtils page = attrService.queryPage(params, catId, AttrEnum.ATTR_TYPE_SALE.getCode());

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
		AttrResponseVO attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVO attr){
//		attrService.save(attr);
		return attrService.saveAttr(attr);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateById(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
		// TODO 删除关联关系表数据

        return R.ok();
    }

}
