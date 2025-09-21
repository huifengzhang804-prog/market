package com.medusa.gruul.global.model.helper;

import io.vavr.control.Option;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 区间MAP 实现类
 *
 * @author 张治保
 * date 2023/2/10
 */
public class RangeMap<K extends Number, V> implements IRangeMap<K, V> {

    private final TreeMap<K, V> delegate;

    public RangeMap() {
        this.delegate = new TreeMap<>(Comparator.comparing(v -> new BigDecimal(v.toString())));
    }


    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return delegate.floorKey((K) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        K floorKey = delegate.floorKey((K) key);
        return floorKey == null ? null : delegate.get(floorKey);
    }

    @Override
    public Option<V> optGet(K key) {
        return Option.of(get(key));
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public <T> void putAll(Collection<T> items, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        items.forEach(item -> delegate.put(keyFunc.apply(item), valueFunc.apply(item)));
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        delegate.putAll(map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }


}
