package com.rkyang.search;

import com.alibaba.fastjson.JSON;
import com.rkyang.search.config.GuliMallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private  RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    /**
     * 保存
     * @throws IOException
     */
    @Test
    void indexData() throws IOException {
        // 创建索引
        IndexRequest indexRequest = new IndexRequest("users");
        // 设置id，不设置会自动生成
        indexRequest.id("1");

        //要保存的文档
        Users users = new Users();
        users.setName("张三");
        users.setGender("男");
        users.setAge(20);
        String usersJson = JSON.toJSONString(users);

        indexRequest.source(usersJson, XContentType.JSON);
        // 执行保存
        IndexResponse index = client.index(indexRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    @ToString
    static class Users {
        private String name;
        private Integer age;
        private String gender;
    }

    /**
     * 条件及聚合检索与分析
     */
    @Test
    void searchTest() throws IOException {
        // 构造search请求对象并指定索引
        SearchRequest searchRequest = new SearchRequest("users");

        // 构造检索DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("age", "20"));

        // 构造聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age"));
        searchSourceBuilder.aggregation(AggregationBuilders.avg("ageAvg").field("age"));

        searchRequest.source(searchSourceBuilder);

        // 执行检索
        SearchResponse search = client.search(searchRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(search);

        // 查询结果分析
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Users users = JSON.parseObject(sourceAsString, Users.class);
            System.out.println("数据ID ==》 " + hit.getId());
            System.out.println(users);
        }
        // 聚合结果分析
        Aggregations aggregations = search.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString + " 人数：" + bucket.getDocCount());
        }

        Avg ageAvg = aggregations.get("ageAvg");
        System.out.println("平均年龄：" + ageAvg.getValue());
    }

    /**
     * 批量保存
     */
    @Test
    void bulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 2; i < 5; i++) {
            Users users = new Users();
            users.setName("测试" + i);
            users.setAge(20);
            users.setGender("男");
            String userJson = JSON.toJSONString(users);

            bulkRequest.add(new IndexRequest("users").id(String.valueOf(i)).source(userJson, XContentType.JSON));
        }

        BulkResponse bulk = client.bulk(bulkRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(bulk);
    }

}
