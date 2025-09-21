package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.search.service.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.dromara.easyes.common.params.SFunction;
import org.dromara.easyes.core.biz.EntityInfo;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.BaseEsMapper;
import org.dromara.easyes.core.kernel.WrapperProcessor;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.dromara.easyes.core.toolkit.FieldUtils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2023/9/25
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final RestHighLevelClient restHighLevelClient;

    @Override
    public <T> void consumerAllMatch(BaseEsMapper<T> mapper, LambdaEsQueryWrapper<T> wrapper, Consumer<T> consumer, Integer size) {
        aggregate(
                size,
                (searchAfter, pageSize) -> mapper.searchAfterPage(wrapper, searchAfter, pageSize),
                consumer
        );
    }

    private <Entity> void aggregate(Integer size, BiFunction<List<Object>, Integer, SAPageInfo<Entity>> function, Consumer<Entity> consumer) {
        SAPageInfo<Entity> pageInfo = function.apply(null, size);
        long remain = pageInfo.getTotal() - size;
        pageInfo.getList().forEach(consumer);
        while (remain > 0) {
            pageInfo = function.apply(pageInfo.getNextSearchAfter(), size);
            remain -= size;
            pageInfo.getList().forEach(consumer);
        }
    }

    @Override
    public boolean refreshIndex(Class<?>... indexClasses) {
        String[] indices = Arrays.stream(indexClasses)
                .map(EntityInfoHelper::getEntityInfo)
                .map(EntityInfo::getIndexName)
                .toArray(String[]::new);
        return this.refreshIndex(indices);
    }

    @Override
    public boolean refreshIndex(String... indices) {
        try {
            RefreshResponse refreshResponse = restHighLevelClient.indices().refresh(new RefreshRequest(indices), RequestOptions.DEFAULT);
            return refreshResponse != null && refreshResponse.getFailedShards() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Map<String, Object> aggregations(Class<T> entityClass, LambdaEsQueryWrapper<T> qw, Collection<SFunction<T, ?>> distinctFields) {
        SearchRequest request = toRequest(entityClass, qw);
        Map<String, Object> aggregationsMap = aggregationsMap(EntityInfoHelper.getEntityInfo(entityClass), request, distinctFields);
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (searchResponse == null) {
            return Map.of();
        }
        renderAggregationsMap(searchResponse, aggregationsMap);
        return aggregationsMap;
    }

    private <T> SearchRequest toRequest(Class<T> entityClass, LambdaEsQueryWrapper<T> qw) {
        SearchRequest request = new SearchRequest();
        request.source(WrapperProcessor.buildSearchSourceBuilder(qw, entityClass));
        return request;
    }


    private <T> Map<String, Object> aggregationsMap(EntityInfo entityInfo, SearchRequest request, Collection<SFunction<T, ?>> distinctFields) {
        String indexName = entityInfo.getIndexName();
        request.indices(indexName);
        // 创建搜索源构建器
        SearchSourceBuilder source = request.source();
        if (source == null) {
            source = new SearchSourceBuilder();
            request.source(source);
        }
        //查询条件聚合
        Map<String, Object> aggregations = new HashMap<>(distinctFields.size());
        if (CollUtil.isNotEmpty(distinctFields)) {
            for (SFunction<T, ?> distinctField : distinctFields) {
                String val = FieldUtils.val(distinctField);
                source.aggregation(AggregationBuilders.terms(val).field(val));
                aggregations.put(val, null);
            }
        }
        return aggregations;
    }

    private void renderAggregationsMap(SearchResponse searchResponse, Map<String, Object> aggregationsMap) {
        if (searchResponse == null) {
            return;
        }
        // 聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        for (String key : aggregationsMap.keySet()) {
            Object aggregation = aggregations.get(key);
            if (!(aggregation instanceof Terms terms)) {
                continue;
            }

            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            if (CollUtil.isEmpty(buckets)) {
                continue;
            }
            aggregationsMap.put(key, buckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList()));
        }
    }

}
