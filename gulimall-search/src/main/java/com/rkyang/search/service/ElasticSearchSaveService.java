package com.rkyang.search.service;

import com.rkyang.common.to.es.SkuEsTO;

import java.util.List;

/**
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
public interface ElasticSearchSaveService {
    boolean saveProductUp(List<SkuEsTO> skuEsTOS);
}
