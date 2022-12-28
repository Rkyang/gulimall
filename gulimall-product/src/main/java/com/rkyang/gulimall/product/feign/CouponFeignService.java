package com.rkyang.gulimall.product.feign;

import com.rkyang.common.to.SkuReductionTO;
import com.rkyang.common.to.SpuBoundTO;
import com.rkyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 积分服务远程调用
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/12/28
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    @PostMapping("coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTO reductionTO);
}
