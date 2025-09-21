package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.search.service.es.entity.EsProductSearchHistoryEntity;
import com.medusa.gruul.search.service.es.mapper.EsProductSearchHistoryMapper;
import com.medusa.gruul.search.service.model.vo.HistoriesAndHotWordsVO;
import com.medusa.gruul.search.service.service.ProductSearchHistoryService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/12/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchHistoryServiceImpl implements ProductSearchHistoryService {


    private final Executor searchCompletableTaskExecutor;
    private final RestHighLevelClient restHighLevelClient;
    private final EsProductSearchHistoryMapper productSearchHistoryMapper;

    @Override
    public HistoriesAndHotWordsVO historiesAndHotWords(Option<Long> userIdOpt) {

        HistoriesAndHotWordsVO hhw = new HistoriesAndHotWordsVO()
                .setHistories(Collections.emptyList())
                .setHotWords(Collections.emptyList());
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        searchCompletableTaskExecutor,
                        () -> userIdOpt.peek(userId ->
                                hhw.setHistories(
                                        productSearchHistoryMapper.selectList(
                                                        EsWrappers.lambdaQuery(EsProductSearchHistoryEntity.class)
                                                                .select(EsProductSearchHistoryEntity::getKeyword)
                                                                .eq(EsProductSearchHistoryEntity::getUserId, userId)
                                                                .orderByDesc(EsProductSearchHistoryEntity::getCreateTime)
                                                                .size(CommonPool.NUMBER_NINE)
                                                ).stream()
                                                .map(EsProductSearchHistoryEntity::getKeyword)
                                                .collect(Collectors.toList())
                                )

                        ),
                        () -> {
                            SearchResponse searchResponse = productSearchHistoryMapper.search(
                                    EsWrappers.lambdaQuery(EsProductSearchHistoryEntity.class)
                                            .matchAllQuery()
                                            .setSearchSourceBuilder(
                                                    SearchSourceBuilder.searchSource()
                                                            .aggregation(
                                                                    AggregationBuilders.terms("wordsTerms")
                                                                            .field("words")
                                                                            .size(10)
                                                            )
                                            )
                            );
                            List<Aggregation> aggregations = searchResponse.getAggregations().asList();
                            if (CollUtil.isEmpty(aggregations)) {
                                return;
                            }
                            hhw.setHotWords(
                                    ((ParsedStringTerms) aggregations.get(0)).getBuckets()
                                            .stream()
                                            .map(bucket -> (String) bucket.getKey())
                                            .collect(Collectors.toList())
                            );
                        }
                )
        );
        return hhw;
    }

    @Override
    public void saveSearchKeywords(Option<Long> userIdOpt, String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return;
        }
        keyword = keyword.trim();
        if (userIdOpt.isDefined()) {
            Integer updateCount = productSearchHistoryMapper.update(null,
                    EsWrappers.lambdaUpdate(EsProductSearchHistoryEntity.class)
                            .eq(EsProductSearchHistoryEntity::getKeyword, keyword)
                            .eq(EsProductSearchHistoryEntity::getUserId, userIdOpt.get())
                            .set(EsProductSearchHistoryEntity::getCreateTime, FastJson2.DATETIME_FORMATTER.format(LocalDateTime.now()))
            );
            if (updateCount > 0) {
                return;
            }
        }
        List<AnalyzeResponse.AnalyzeToken> tokens;
        try {
            tokens = restHighLevelClient.indices()
                    .analyze(AnalyzeRequest.withGlobalAnalyzer(Analyzer.IK_SMART, keyword), RequestOptions.DEFAULT)
                    .getTokens();
        } catch (IOException ex) {
            log.error("分词异常", ex);
            return;
        }
        EsProductSearchHistoryEntity historyEntity = new EsProductSearchHistoryEntity();
        historyEntity.setUserId(userIdOpt.getOrNull())
                .setKeyword(keyword)
                .setWords(tokens.stream().map(AnalyzeResponse.AnalyzeToken::getTerm).collect(Collectors.toSet()))
                .setCreateTime(LocalDateTime.now());
        productSearchHistoryMapper.insert(historyEntity);
    }

    @Override
    public void deleteHistoryByUserId(Long userId) {
        productSearchHistoryMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsProductSearchHistoryEntity.class)
                        .eq(EsProductSearchHistoryEntity::getUserId, userId)
                        .set(EsProductSearchHistoryEntity::getUserId, null)
        );
    }
}
