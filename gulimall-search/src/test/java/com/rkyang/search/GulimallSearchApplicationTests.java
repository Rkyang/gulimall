package com.rkyang.search;

import com.rkyang.search.config.GuliMallElasticSearchConfig;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private GuliMallElasticSearchConfig config;

    @Test
    void contextLoads() {
        RestClient esRestClient = config.getEsRestClient();
        System.out.println(esRestClient);
    }

}
