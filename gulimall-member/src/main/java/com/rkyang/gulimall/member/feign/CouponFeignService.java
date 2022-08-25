package com.rkyang.gulimall.member.feign;

import com.rkyang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 声明式远程调用，openFeign
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/8/25
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @GetMapping("coupon/coupon/member/list")
    R memberCoupon();
}
