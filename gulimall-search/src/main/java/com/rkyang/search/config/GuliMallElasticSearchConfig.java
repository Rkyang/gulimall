package com.rkyang.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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
     * 请求参数
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 获取es的client
     */
    @Bean
    public RestHighLevelClient getEsRestClient() {
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost("172.16.20.172", 9200, "http")
        ));
    }
}
