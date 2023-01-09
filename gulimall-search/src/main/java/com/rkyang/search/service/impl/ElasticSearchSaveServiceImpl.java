package com.rkyang.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.rkyang.common.to.es.SkuEsTO;
import com.rkyang.search.config.GuliMallElasticSearchConfig;
import com.rkyang.search.service.ElasticSearchSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rkyang (rkyang@outlook.com)
 * @date 2023/1/9
 */
@Slf4j
@Service
public class ElasticSearchSaveServiceImpl implements ElasticSearchSaveService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean saveProductUp(List<SkuEsTO> skuEsTOS) {
        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsTO esTO : skuEsTOS) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(esTO.getSkuId().toString());
            String esTOJson = JSON.toJSONString(esTO);
            indexRequest.source(esTOJson, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);
            boolean hasFailures = bulkResponse.hasFailures();
            if (hasFailures) {
                List<String> failedIdList = new ArrayList<>();
                for (BulkItemResponse bulk : bulkResponse) {
                    if (bulk.isFailed()) {
                        failedIdList.add(bulk.getId());
                    }
                }
                log.error("SKU上架，ES保存失败的数据ID：{}", failedIdList);
            }
        } catch (IOException e) {
            log.error("sku上架，ES数据保存出现异常：{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
