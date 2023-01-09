package com.rkyang.gulimall.product.feign;

import com.rkyang.common.to.es.SkuEsTO;
import com.rkyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 检索服务远程调用
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product/up")
    R saveProductUp(List<SkuEsTO> skuEsTOS);
}
