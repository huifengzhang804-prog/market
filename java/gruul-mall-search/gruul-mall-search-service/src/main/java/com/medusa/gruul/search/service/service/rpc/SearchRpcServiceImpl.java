package com.medusa.gruul.search.service.service.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.search.api.model.*;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.search.service.es.entity.EsProductActivityEntity;
import com.medusa.gruul.search.service.es.entity.EsProductEntity;
import com.medusa.gruul.search.service.es.mapper.EsProductActivityMapper;
import com.medusa.gruul.search.service.es.mapper.EsProductMapper;
import com.medusa.gruul.search.service.service.EsProductService;
import com.medusa.gruul.search.service.service.ProductActivityService;
import com.medusa.gruul.search.service.service.SearchService;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保 date 2023/3/22
 */
@Service
@DubboService
@RequiredArgsConstructor
@Slf4j
public class SearchRpcServiceImpl implements SearchRpcService {

    private final EsProductMapper esProductMapper;
    private final EsProductActivityMapper esProductActivityMapper;
    private final SearchService searchService;
    private final EsProductService esProductService;
    private final ProductActivityService productActivityService;

    @Override
    public Set<Long> getSellProductShopIds() {
        Set<Long> shopIds = new HashSet<>();
        searchService.consumerAllMatch(
                esProductMapper,
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        //查询店铺 id
                        .select(EsProductEntity::getShopId)
                        //状态正常的商品
                        .eq(EsProductEntity::getStatus, ProductStatus.SELL_ON)
                        //库存类型为无限制或者库存大于0
                        .and(
                                wrapper ->
                                        wrapper.eq(EsProductEntity::getStockTypes, StockType.UNLIMITED)
                                                .or()
                                                .gt(EsProductEntity::getStocks, CommonPool.NUMBER_ZERO)
                        )
                        //去重
                        .distinct(EsProductEntity::getShopId)
                        //按店铺 id 升序
                        .orderByAsc(EsProductEntity::getShopId),
                product -> shopIds.add(product.getShopId())
        );
        return shopIds;
    }

    @Override
    public void activityBind(ProductActivityBind bindParam) {
        Long shopId = bindParam.getShopId();
        LocalDateTime startTime = bindParam.getStartTime();
        LocalDateTime endTime = bindParam.getEndTime();
        Set<Long> productIds = bindParam.getProductIds();
        Long count = esProductActivityMapper.selectCount(
                EsWrappers.lambdaQuery(EsProductActivityEntity.class)
                        .eq(EsProductActivityEntity::getShopId, shopId)
                        .in(EsProductActivityEntity::getProductId, productIds)
                        .lt(EsProductActivityEntity::getStartTime, FastJson2.DATETIME_FORMATTER.format(endTime))
                        .gt(EsProductActivityEntity::getEndTime, FastJson2.DATETIME_FORMATTER.format(startTime))
        );
        if (count > 0) {
            throw new GlobalException("该时间段内存在活动冲突");
        }
        esProductActivityMapper.insertBatch(
                productIds.stream()
                        .map(productId -> new EsProductActivityEntity()
                                .setActivityType(bindParam.getActivityType())
                                .setActivityId(bindParam.getActivityId())
                                .setShopId(shopId)
                                .setProductId(productId)
                                .setStartTime(startTime)
                                .setEndTime(endTime)
                        )
                        .toList()
        );
    }

    @Override
    public void activityUnbind(Set<ProductActivityUnbind> unbindParams) {
        LambdaEsQueryWrapper<EsProductActivityEntity> queryWrapper = EsWrappers.lambdaQuery(EsProductActivityEntity.class);
        for (ProductActivityUnbind unbindParam : unbindParams) {
            queryWrapper.or(
                    wrapper -> wrapper.eq(EsProductActivityEntity::getActivityType, unbindParam.getActivityType())
                            .eq(EsProductActivityEntity::getActivityId, unbindParam.getActivityId())
                            .eq(EsProductActivityEntity::getShopId, unbindParam.getShopId())
            );
        }
        esProductActivityMapper.delete(queryWrapper);

    }

    /**
     * 修改商品是否在分销列表状态
     *
     * @param productDistributed 参数
     */
    @Override
    public void updateProduct(ProductDistributed productDistributed) {
        esProductMapper.update(null,
                EsWrappers.lambdaUpdate(EsProductEntity.class)
                        .eq(EsProductEntity::getShopId, productDistributed.getShopId())
                        .in(EsProductEntity::getProductId, productDistributed.getProductIds())
                        .set(EsProductEntity::getIsDistributed, productDistributed.getIsDistributed()));
    }

    /**
     * 查询商品最近有库存的活动
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 活动信息
     */
    @Override
    public ProductActivityVO getRecentActivity(Long shopId, Long productId) {
        return productActivityService.getRecentActivity(shopId, productId);
    }

    @Override
    public Map<Long, CategoryStaticVo> categoryCount(CategoryCountParam param) {
        return esProductService.categoryCount(param);
    }

    @Override
    public Map<Long, List<ProductVO>> salesRanking(Set<Long> shopIds, Integer salesRanking) {
        if (CollUtil.isEmpty(shopIds) || salesRanking <= 0) {
            return Map.of();
        }
        String shopIdFieldName = "shopId";
        String salesVolumeFieldName = "salesVolume";
//        SearchResponse response 
        SearchResponse response = esProductMapper.search(
                EsWrappers.lambdaQuery(EsProductEntity.class)
                        .setSearchSourceBuilder(
                                SearchSourceBuilder.searchSource()
                                        .query(
                                                QueryBuilders.boolQuery()
                                                        .must(QueryBuilders.termsQuery(shopIdFieldName, shopIds))
                                                        .must(QueryBuilders.termQuery("status.keyword", ProductStatus.SELL_ON.name()))
                                        )
                                        // 不返回文档，只返回聚合结果
                                        .size(CommonPool.NUMBER_ZERO)
                                        .aggregation(
                                                AggregationBuilders.terms(shopIdFieldName).field(shopIdFieldName)
                                                        .subAggregation(
                                                                AggregationBuilders.topHits(salesVolumeFieldName).size(salesRanking)
                                                                        .sort(salesVolumeFieldName, SortOrder.DESC)
                                                        )
                                        )

                        )
        );
        Map<Long, List<ProductVO>> result = MapUtil.newHashMap();
        // 从搜索结果中获取聚合数据
        Map<String, Aggregation> aggregations = response.getAggregations().asMap();
        ParsedLongTerms storesAgg = (ParsedLongTerms) aggregations.get(shopIdFieldName);
        for (Terms.Bucket storeBucket : storesAgg.getBuckets()) {
            // 获取店铺ID
            Long shopId = storeBucket.getKeyAsNumber().longValue();
            // 获取店铺销量前几名的商品
            ParsedTopHits topSalesAgg = storeBucket.getAggregations().get(salesVolumeFieldName);
            for (SearchHit hit : topSalesAgg.getHits().getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                result.computeIfAbsent(shopId, (key) -> CollUtil.newArrayList())
                        .add(FastJson2.convert(sourceAsMap, ProductVO.class));
            }
        }
        return result;
    }

    @Override
    public List<Long> queryHasSellOnProductShopIds(List<Long> shopIds) {
      // 构建查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
            .filter(QueryBuilders.termQuery("status.keyword", ProductStatus.SELL_ON.name())) // 商品状态是上架
            .filter(QueryBuilders.termsQuery("shopId",shopIds ));

        // 构建 Terms 聚合，对店铺 ID 去重
        TermsAggregationBuilder termsAggregation = AggregationBuilders
            .terms("uniqueShopIds") // 聚合名称
            .field("shopId") // 按店铺 ID 字段分组
            .size(shopIds.size()); // 设置返回的桶的数量

        // 构建查询请求
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            .query(boolQuery)
            .aggregation(termsAggregation)
            .size(0); // 不需要返回具体的文档，设置为 0

        SearchRequest searchRequest = new SearchRequest(
                EntityInfoHelper.getEntityInfo(EsProductEntity.class).getIndexName());
        searchRequest.source(sourceBuilder);

        // 执行查询
        SearchResponse searchResponse = null;
        try {
            searchResponse = esProductMapper.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         List<Long> result= Lists.newArrayList();
        // 处理聚合结果
        var agg = searchResponse.getAggregations().get("uniqueShopIds");
        if (agg instanceof org.elasticsearch.search.aggregations.bucket.terms.Terms terms) {
            for (var bucket : terms.getBuckets()) {
                log.info("店铺 ID: " + bucket.getKeyAsString() + ", 商品数量: " + bucket.getDocCount());
                result.add(Long.valueOf(bucket.getKeyAsString()));
            }
        }
        return result;
    }

}
