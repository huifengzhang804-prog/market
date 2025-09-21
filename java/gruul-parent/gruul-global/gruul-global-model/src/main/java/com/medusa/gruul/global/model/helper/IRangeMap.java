package com.medusa.gruul.global.model.helper;

import io.vavr.control.Option;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * 区间MAP
 *
 * @author 张治保
 * date 2023/2/10
 */
public interface IRangeMap<K, V> extends Map<K, V> {

    /**
     * 把集合数据put 到区间map里
     *
     * @param items     集合数据
     * @param keyFunc   获取集合里每个item的 key 函数
     * @param valueFunc 获取集合里每个item的 value 函数
     * @param <T>       集合里每个item的类型
     */
    <T> void putAll(Collection<T> items, Function<T, K> keyFunc, Function<T, V> valueFunc);


    /**
     * 获取可能为空的值
     *
     * @param key key
     * @return 可能为null的值
     */
    Option<V> optGet(K key);
}
