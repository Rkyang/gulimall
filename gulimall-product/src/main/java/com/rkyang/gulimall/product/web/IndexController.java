package com.rkyang.gulimall.product.web;

import com.rkyang.gulimall.product.entity.CategoryEntity;
import com.rkyang.gulimall.product.service.CategoryService;
import com.rkyang.gulimall.product.vo.Catalog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 首页
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/10
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 首页
     */
    @RequestMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntities = categoryService.selectLevel1();
        model.addAttribute("catalogs", categoryEntities);
        return "index";
    }

    /**
     * 二级、三级分类数据
     */
    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalog2And3Json() {
        return categoryService.getCatalog2And3();
    }
}
