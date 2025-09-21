package com.medusa.gruul.search.service.service;

import com.medusa.gruul.search.service.model.SearchConst;
import org.dromara.easyes.common.params.SFunction;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.BaseEsMapper;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author 张治保
 * @since 2023/9/25
 */
public interface SearchService {

    /**
     * 消费所有匹配项数据 基于search_after
     *
     * @param mapper   base mapper
     * @param wrapper  查询条件组装器 需要设置排序规则
     *                 使用searchAfter必须指定排序,若没有排序不仅会报错,
     *                 而且对跳页也不友好. 需要保持searchAfter排序唯一,不然会导致分页失效,
     *                 推荐使用id,uuid等进行排序.
     * @param consumer 消费任务
     * @param <T>      索引数据类型
     */
    default <T> void consumerAllMatch(BaseEsMapper<T> mapper, LambdaEsQueryWrapper<T> wrapper, Consumer<T> consumer) {
        this.consumerAllMatch(mapper, wrapper, consumer, SearchConst.AGGREGATION_MAX_SIZE);
    }

    /**
     * 消费所有匹配项数据
     *
     * @param mapper   base mapper
     * @param wrapper  查询条件组装器
     * @param size     分页尺寸
     * @param consumer 消费任务
     * @param <T>      索引数据类型
     */
    <T> void consumerAllMatch(BaseEsMapper<T> mapper, LambdaEsQueryWrapper<T> wrapper, Consumer<T> consumer, Integer size);

    /**
     * 刷新索引
     *
     * @param indexClasses 索引类 可以是多个
     * @return 是否刷新成功
     */
    boolean refreshIndex(Class<?>... indexClasses);


    /**
     * 刷新索引
     *
     * @param indices 索引名称 可以是多个
     * @return 是否刷新成功
     */
    boolean refreshIndex(String... indices);

    /**
     * 聚合查询
     *
     * @param entityClass    实体类
     * @param qw             查询条件
     * @param distinctFields 聚合字段
     * @param <T>            实体类 类型
     * @return 聚合结果
     */
    <T> Map<String, Object> aggregations(Class<T> entityClass, LambdaEsQueryWrapper<T> qw, Collection<SFunction<T, ?>> distinctFields);


}
