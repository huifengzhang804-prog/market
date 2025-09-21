package com.medusa.gruul.common.redis.util;

import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 用于判断是否存在的工具 通过 redis zset实现
 *
 * @author 张治保
 * @since 2024/6/21
 */
@Slf4j
@Getter
@ToString
@RequiredArgsConstructor
public class RedisExist<T> implements Serializable, InitializingBean {

    /**
     * redis key
     */
    private final String key;

    /**
     * 定期清理数据的时间间隔
     * 设置这个数据才会定期清理数据 为null则不清理
     * 和 maxExistDuration 搭配一起使用
     */
    private Duration clearDuration;

    /**
     * 最多的存储时间
     */
    @Setter
    private Duration maxExistDuration;


    /**
     * 开启定时清理功能
     *
     * @param clearDuration    定期清理数据的时间间隔
     * @param maxExistDuration 最多的存储时间 距当前时间超过这个范围会被清理
     */
    public final void autoClear(Duration clearDuration, Duration maxExistDuration) {
        this.clearDuration = clearDuration;
        this.maxExistDuration = maxExistDuration;
    }


    /**
     * 添加数据
     *
     * @param values 数据 可以同时设置多个值
     */
    @SafeVarargs
    public final void add(T... values) {
        long mills = System.currentTimeMillis();
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>(values.length);
        for (T value : values) {
            tuples.add(
                    new DefaultTypedTuple<>(value, (double) mills)
            );
        }
        RedisUtil.getRedisTemplate()
                .opsForZSet()
                .add(key, tuples);
    }

    /**
     * 判断是值否存在 单个
     *
     * @param value 值
     * @return 是否存在
     */
    public boolean exists(T value) {
        Double score = RedisUtil.getRedisTemplate()
                .opsForZSet()
                .score(key, value);
        return score != null;
    }

    /**
     * 判断值是否存在 批量
     *
     * @param values 值集合
     * @return 存在的值
     */
    public Set<T> exists(Collection<T> values) {
        //转为 list
        List<T> listValues = values instanceof List<T> list ? list : List.copyOf(values);

        List<Double> scores = RedisUtil.getRedisTemplate()
                .opsForZSet()
                .score(key, listValues.toArray(Object[]::new));
        Set<T> result = new HashSet<>(listValues.size());
        boolean scoreNotNull = scores != null;
        for (int i = 0; i < listValues.size(); i++) {
            if (scoreNotNull && scores.get(i) != null) {
                result.add(listValues.get(i));
            }
        }
        return result;
    }

    /**
     * 删除数据
     *
     * @param values 数据 可以同时删除多个值
     */
    @SafeVarargs
    public final void remove(T... values) {
        RedisUtil.getRedisTemplate()
                .opsForZSet()
                .remove(key, (Object[]) values);
    }

    /**
     * 手动清除过期数据
     */
    public final void clearExpired() {
        if (maxExistDuration == null) {
            throw new RuntimeException("maxExistDuration cannot be null");
        }
        log.debug("clearing expired cache of key:{}", key);
        RedisUtil.getRedisTemplate().opsForZSet()
                .removeRangeByScore(
                        key,
                        0,
                        System.currentTimeMillis() - maxExistDuration.toMillis()
                );
    }


    @Override
    public void afterPropertiesSet() {
        if (clearDuration == null) {
            return;
        }
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(
                        this::clearExpired,
                        CommonPool.NUMBER_ZERO,
                        clearDuration.toSeconds(),
                        TimeUnit.SECONDS
                );
    }


}
