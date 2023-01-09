package com.rkyang.gulimall.product.feign;

import com.rkyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 库存服务远程调用
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasSkuStock")
    R hasSkuStock(@RequestBody List<Long> skuId);
}
