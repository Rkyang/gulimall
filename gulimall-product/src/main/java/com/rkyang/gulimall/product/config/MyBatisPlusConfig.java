package com.rkyang.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis plus 配置类
 * @author rkyang (rkyang@outlook.com)
 * @date 2022/10/26
 */
@Configuration
// 开启事务
@EnableTransactionManagement
@MapperScan("com.rkyang.gulimall.product.dao")
public class MyBatisPlusConfig {

    /**
     * 引入分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 请求的页面大于最后页时，是否回到首页，默认false
        paginationInterceptor.setOverflow(true);
        // 最大单页限制数量，默认500，-1不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
