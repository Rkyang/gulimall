package com.rkyang.search.controller;

import com.rkyang.common.exception.StatusCodeEnum;
import com.rkyang.common.to.es.SkuEsTO;
import com.rkyang.common.utils.R;
import com.rkyang.search.service.ElasticSearchSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ES保存
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
@RequestMapping("/search/save")
@RestController
public class ElasticSearchSaveController {

    @Autowired
    private ElasticSearchSaveService searchSaveService;

    @PostMapping("/product/up")
    public R saveProductUp(List<SkuEsTO> skuEsTOS) {
        boolean result = searchSaveService.saveProductUp(skuEsTOS);
        if (!result) {
            return R.error(StatusCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), StatusCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
        return R.ok();
    }
}
