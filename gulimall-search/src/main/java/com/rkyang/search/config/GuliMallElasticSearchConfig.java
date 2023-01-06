package com.rkyang.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * elasticsearch 配置类
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/6
 */
@Configuration
public class GuliMallElasticSearchConfig {

    /**
     * 获取es的client
     */
    @Bean
    public RestClient getEsRestClient() {
        return RestClient.builder(
                new HttpHost("172.16.20.172", 9200, "http")
        ).build();
    }
}
