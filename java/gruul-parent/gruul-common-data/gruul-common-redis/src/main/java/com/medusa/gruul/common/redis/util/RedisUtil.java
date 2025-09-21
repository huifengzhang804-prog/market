package com.medusa.gruul.common.redis.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.data.redis.FastJsonRedisSerializer;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.HashKeySerializer;
import com.medusa.gruul.common.redis.constant.RedisConstant;
import com.medusa.gruul.common.redis.constant.ResultSerialType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Redis工具类
 *
 * @author 张治保
 * 参考 ruoyi代码
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtil {

    /**
     * 当前运行的应用名称
     */
    private static String applicationName;
    /**
     * redis 客户端
     */
    private static RedisTemplate redisTemplate;

    /**
     * redisson 客户端
     */
    private static RedissonClient redissonClient;

    /**
     * 缓存延迟双删的核心线程数 默认为8
     */
    private static int doubleDeletionScheduleCorPoolSize = 8;


    /**
     * 生成redis key 以':'拼接 为空的key不拼接
     *
     * @param keys key参数列表
     * @return 拼接后的key
     */
    public static String key(Object... keys) {
        if (keys == null) {
            return StrUtil.EMPTY;
        }
        StringJoiner joiner = new StringJoiner(StrPool.COLON);
        for (Object key : keys) {
            joiner.add(String.valueOf(key));
        }
        return joiner.toString();
    }

    /**
     * 生成redis key 以':'拼接 为空的key不拼接
     *
     * @param keys key参数列表
     * @return 拼接后的key
     */
    public static String key(String... keys) {
        return RedisUtil.key((Object[]) keys);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public static <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 自增 当 incrementedConsumer 执行异常时 回滚自增
     *
     * @param key                 缓存key
     * @param incrementedConsumer 自增后执行的方法
     * @param delta               自增步长
     */
    public static void increment(String key, long delta, Consumer<Long> incrementedConsumer) {
        increment(
                key,
                delta,
                incr -> {
                    incrementedConsumer.accept(incr);
                    return null;
                }
        );
    }

    /**
     * 自增 当 incrementedFunction 执行异常时 回滚自增
     *
     * @param key                 缓存key
     * @param incrementedFunction 自增后执行的方法
     * @param delta               自增步长
     * @param <T>                 incrementedFunction 返回结果类型
     * @return incrementedFunction 返回结果
     */
    public static <T> T increment(String key, long delta, Function<Long, T> incrementedFunction) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Long increment = valueOperations.increment(key, delta);
        try {
            return incrementedFunction.apply(increment);
        } catch (Exception e) {
            valueOperations.decrement(key, delta);
            throw e;
        }
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout, final TimeUnit unit) {
        Boolean success = redisTemplate.expire(key, timeout, unit);
        return success != null && success;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 批量删除 redis 缓存数据
     *
     * @param keys key 动态数组
     * @return 删除成功数量
     */
    public static long delete(final String... keys) {
        return RedisUtil.delete(Set.of(keys));
    }

    /**
     * 批量删除 redis 缓存数据
     *
     * @param keys redis key集合
     * @return 删除成功数量
     */
    public static long delete(final Collection keys) {
        Long success = redisTemplate.delete(keys);
        return success != null ? success : 0;
    }

    /**
     * (异步)删除 redis 缓存数据 执行速度比较快 由 redis 后台异步执行
     *
     * @param keys redis key集合
     * @return 删除成功数量
     */
    public static long unlink(final String... keys) {
        return RedisUtil.unlink(Set.of(keys));
    }


    /**
     * (异步)删除 redis 缓存数据 执行速度比较快 由 redis 后台异步执行
     *
     * @param keys redis key集合
     * @return 删除成功数量
     */
    public static long unlink(final Collection keys) {
        Long success = redisTemplate.unlink(keys);
        return success != null ? success : 0;
    }

    /**
     * pipeline模式 执行缓存 使用 value方式反序列化结果
     *
     * @param tasks 执行任务
     * @return 执行结果
     */
    public static List<Object> executePipelined(final Consumer<RedisTemplate<String, Object>>... tasks) {
        return executePipelined(ResultSerialType.VALUE, tasks);
    }


    /**
     * pipeline模式 执行缓存
     *
     * @param resultSerialType 执行结果序列化方式
     * @param tasks            执行任务
     * @return 执行结果
     */
    public static List<Object> executePipelined(final ResultSerialType resultSerialType, final Consumer<RedisTemplate<String, Object>>... tasks) {
        RedisTemplate<String, Object> template = RedisUtil.getRedisTemplate();
        return template.executePipelined(
                new SessionCallback<>() {
                    @Override
                    public <K, V> Object execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                        for (Consumer<RedisTemplate<String, Object>> task : tasks) {
                            task.accept((RedisTemplate<String, Object>) operations);
                        }
                        return null;
                    }
                },
                resultSerialType == ResultSerialType.KEY ? template.getKeySerializer() : template.getValueSerializer()
        );
    }


    /**
     * 固定时间加上随机数 以免发生同时大面积缓存失效
     */
    public static long expireWithRandom(long fixedTime, long randomRange) {
        return fixedTime + RandomUtil.randomLong(0, randomRange);
    }

    /**
     * 固定时间加上随机数 以免发生同时大面积缓存失效
     */
    public static long expireWithRandom(long fixedTime) {
        return RedisUtil.expireWithRandom(fixedTime, fixedTime);
    }


    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }


    public static <T> List<T> getCacheList(final String key, Class<T> type) {
        List<?> maps = redisTemplate.opsForList().range(key, 0, -1);
        return CollUtil.emptyIfNull(maps).stream().map(map -> RedisUtil.toBean(map, type)).collect(Collectors.toList());
    }

    public static <T> List<T> getCacheList(final String key, TypeReference<T> type) {
        List<?> maps = redisTemplate.opsForList().range(key, 0, -1);
        return CollUtil.emptyIfNull(maps).stream().map(map -> RedisUtil.toBean(map, type)).collect(Collectors.toList());
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }

        return setOperation;
    }

    /**
     * 获得缓存的set
     */
    public static <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public static <T> Set<T> getCacheSet(final String key, Class<T> type) {
        Set<?> maps = redisTemplate.opsForSet().members(key);
        return CollUtil.emptyIfNull(maps).stream().map(map -> RedisUtil.toBean(map, type)).collect(Collectors.toSet());
    }

    public static <T> Set<T> getCacheSet(final String key, TypeReference<T> type) {
        Set<?> maps = redisTemplate.opsForSet().members(key);
        return CollUtil.emptyIfNull(maps).stream().map(map -> RedisUtil.toBean(map, type)).collect(Collectors.toSet());
    }

    /**
     * 缓存ZSet
     *
     * @param key    缓存键值
     * @param tuples 缓存的数据
     */
    public static <T> void setCacheZSet(final String key, final Set<ZSetOperations.TypedTuple<T>> tuples) {
        BoundZSetOperations boundZSetOperations = redisTemplate.boundZSetOps(key);
        boundZSetOperations.add(tuples);
    }

    public static <T> List<T> getCacheZSet(final String key, long pageNum, long pageSize, Class<T> type) {
        Set<?> maps = redisTemplate.opsForZSet().range(key, pageNum, pageSize);
        return CollUtil.emptyIfNull(maps).stream().map(map -> RedisUtil.toBean(map, type)).collect(Collectors.toList());
    }

    /**
     * 缓存Map
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    public static <T> void setCacheMap(final String key, final T object) {
        if (object != null) {
            redisTemplate.opsForHash().putAll(key, RedisUtil.toBean(object, Map.class));
        }
    }

    /**
     * 缓存 map 并设置 过期时间
     *
     * @param key    缓存key
     * @param object 缓存对象
     * @param time   过期时间
     * @param <T>    除基础数据类型、其封装类与String等外的对象
     */
    public static <T> void setCacheMap(final String key, final T object, Duration time) {
        RedisUtil.executePipelined(
                redisOperations -> redisOperations.opsForHash().putAll(key, RedisUtil.toBean(object, Map.class)),
                redisOperations -> redisOperations.expire(key, time)
        );
    }


    /**
     * 读取缓存
     * 如果缓存不为空 立即返回
     * 为空时加锁 二次从缓存种获取数据 如果不为空返回 为空则从factory获取数据并设置缓存
     *
     * @param cacheFactory  缓存获取方法
     * @param factory       数据获取方法
     * @param cacheConsumer 缓存设置方法
     * @param lockKey       针对缓存数据的锁key
     * @param <T>           缓存数据类型
     * @return 缓存数据
     */
    public static <T> T getCache(
            Supplier<T> cacheFactory,
            Supplier<T> factory,
            Consumer<T> cacheConsumer,
            String lockKey
    ) {
        T cacheValue = cacheFactory.get();
        if (cacheValue != null) {
            return cacheValue;
        }
        return lockRun(
                lockKey,
                () -> {
                    // 二次检查缓存
                    T cacheValueLocked = cacheFactory.get();
                    if (cacheValueLocked != null) {
                        return cacheValueLocked;
                    }
                    T data = factory.get();
                    if (data == null) {
                        return null;
                    }
                    cacheConsumer.accept(data);
                    return data;
                }
        );
    }

    /**
     * 读取缓存
     * 如果缓存不为空 立即返回
     * 为空时加锁 二次从缓存种获取数据 如果不为空返回 为空则从factory获取数据并设置缓存
     *
     * @param cacheFactory 缓存获取方法
     * @param factory      数据获取方法
     * @param expireTime   缓存过期时间
     * @param key          缓存key
     * @param <T>          缓存数据类型
     * @return 缓存数据
     */
    public static <T> T getCache(
            Supplier<T> cacheFactory,
            Supplier<T> factory,
            Duration expireTime,
            String key
    ) {
        return getCache(
                cacheFactory,
                factory,
                data -> {
                    if (data instanceof String || ObjectUtil.isBasicType(data)) {
                        RedisUtil.setCacheObject(key, data);
                        return;
                    }
                    RedisUtil.setCacheMap(key, data, expireTime);
                },
                RedisUtil.key(key, RedisUtilConstant.QUERY_LOCK)
        );
    }

    public static <T> T getCacheMap(Class<T> type, Supplier<T> factory, Duration expireTime, String key) {
        return RedisUtil.getCache(
                () -> RedisUtil.getCacheMap(key, type),
                factory,
                expireTime,
                key
        );
    }

    public static <T> T getCacheMap(
            TypeReference<T> reference,
            Supplier<T> factory,
            Duration expireTime,
            String key
    ) {
        return RedisUtil.getCache(
                () -> RedisUtil.getCacheMap(key, reference),
                factory,
                expireTime,
                key
        );
    }

    /**
     * 读取缓存 并设置缓存
     *
     * @param type       缓存数据类型
     * @param factory    缓存获取方法
     * @param expireTime 过期时间
     * @param keys       以':'拼接缓存key
     * @param <T>        缓存数据类型
     * @return 缓存数据
     */
    public static <T> T getCacheMap(
            Class<T> type,
            Supplier<T> factory,
            Duration expireTime,
            Object... keys
    ) {
        return RedisUtil.getCacheMap(type, factory, expireTime, RedisUtil.key(keys));
    }

    /**
     * 读取缓存 并设置缓存
     *
     * @param reference  缓存数据类型
     * @param factory    缓存获取方法
     * @param expireTime 过期时间
     * @param keys       以':'拼接缓存key
     * @param <T>        缓存数据类型
     * @return 缓存数据
     */
    public static <T> T getCacheMap(
            TypeReference<T> reference,
            Supplier<T> factory,
            Duration expireTime,
            Object... keys
    ) {
        return RedisUtil.getCacheMap(reference, factory, expireTime, RedisUtil.key(keys));
    }

    /**
     * 获得缓存的Map
     */
    public static <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public static <T> T getCacheMap(final String key, Class<T> type) {
        Map entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return RedisUtil.toBean(entries, type);
    }

    public static <T> T getCacheMap(final String key, TypeReference<T> type) {
        Map entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return RedisUtil.toBean(entries, type);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    public static <T> T getCacheMapValue(final String key, final String hKey, Class<T> type) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return RedisUtil.toBean(opsForHash.get(key, hKey), type);
    }

    public static <T> T getCacheMapValue(final String key, final String hKey, TypeReference<T> type) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return RedisUtil.toBean(opsForHash.get(key, hKey), type);
    }

    /**
     * 删除Hash中的数据
     */
    public static void delCacheMapValue(final String key, final String... hashKey) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, (Object[]) hashKey);
    }

    /**
     * 缓存延迟双删
     *
     * @param factory 目标任务
     * @param keys    插入':' 拼接成需要删除的缓存key
     * @param <T>     any object
     * @return 目标任务返回结果
     */
    public static <T> T doubleDeletion(Supplier<T> factory, Object... keys) {
        return RedisUtil.doubleDeletion(
                factory,
                () -> RedisUtil.getRedisTemplate().delete(
                        RedisUtil.key(keys)
                )
        );
    }

    /**
     * 缓存延迟双删
     *
     * @param task 目标任务
     * @param keys 插入':' 拼接成需要删除的缓存key
     */
    public static void doubleDeletion(Runnable task, Object... keys) {
        RedisUtil.doubleDeletion(
                task,
                () -> RedisUtil.getRedisTemplate().delete(
                        RedisUtil.key(keys)
                )
        );
    }

    /**
     * 缓存延迟双删
     *
     * @param factory 目标任务
     * @param key     需要删除的缓存key
     * @param <T>     any object
     * @return 目标任务返回结果
     */
    public static <T> T doubleDeletion(Supplier<T> factory, String key) {
        return RedisUtil.doubleDeletion(
                factory,
                () -> RedisUtil.getRedisTemplate().delete(key)
        );
    }

    /**
     * 缓存延迟双删
     *
     * @param task 目标任务
     * @param key  需要删除的缓存key
     */
    public static void doubleDeletion(Runnable task, String key) {
        RedisUtil.doubleDeletion(
                task,
                () -> RedisUtil.getRedisTemplate().delete(key)
        );
    }

    /**
     * 缓存延迟双删
     *
     * @param factory         执行的目标任务
     * @param deleteCacheTask 清除缓存的任务
     * @param <T>             any object
     * @return 执行目标任务的返回结果
     */
    public static <T> T doubleDeletion(Supplier<T> factory, Runnable deleteCacheTask) {
        deleteCacheTask.run();
        T data = factory.get();
        //延迟800毫秒秒再删一次
        DelayExecutor.DELAY_EXECUTOR.schedule(deleteCacheTask, 800, TimeUnit.MILLISECONDS);
        return data;
    }

    /**
     * 缓存延迟双删
     *
     * @param task            目标任务
     * @param deleteCacheTask 清楚缓存任务
     */
    public static void doubleDeletion(Runnable task, Runnable deleteCacheTask) {
        RedisUtil.doubleDeletion(
                () -> {
                    task.run();
                    return null;
                },
                deleteCacheTask
        );
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public static <T> List<T> getMultiCacheMapValue(final String key, final Collection<?> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public static <T> List<T> getMultiCacheMapValue(final String key, final Collection<?> hKeys, Class<T> type) {
        List<?> list = redisTemplate.opsForHash().multiGet(key, hKeys);
        return CollUtil.emptyIfNull(list).stream().map(li -> RedisUtil.toBean(li, type)).toList();
    }

    public static <T> List<T> getMultiCacheMapValue(final String key, final Collection<?> hKeys, TypeReference<T> type) {
        List<?> list = redisTemplate.opsForHash().multiGet(key, hKeys);
        return CollUtil.emptyIfNull(list).stream().map(li -> RedisUtil.toBean(li, type)).collect(Collectors.toList());
    }

    /**
     * c
     * 计算两个坐标的距离 单位米
     */
    public static Distance distance(Point p1, Point p2) {
        String key = IdUtil.fastSimpleUUID();
        List<Object> result = RedisUtil.executePipelined(
                redisOperations -> {
                    GeoOperations geoOperations = redisOperations.opsForGeo();
                    geoOperations.add(key, p1, 0);
                    geoOperations.add(key, p2, 1);
                    geoOperations.distance(key, 0, 1, RedisGeoCommands.DistanceUnit.METERS);
                    redisOperations.delete(key);
                }
        );
        return ((Distance) result.get(2));
    }


    /**
     * 根据 通配符获取匹配到的所有 redis key
     *
     * @param patterns 字符串前缀动态数组
     * @return 对象列表
     */
    public static Set<String> keys(final String... patterns) {
        return keys(Set.of(patterns));
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param patterns 字符串前缀 列表
     * @return 对象列表
     */
    public static Set<String> keys(final Set<String> patterns) {
        if (patterns.size() == CommonPool.NUMBER_ONE) {
            return redisTemplate.keys(patterns.iterator().next());
        }
        List<Object> keys = RedisUtil.executePipelined(
                ResultSerialType.KEY,
                operations -> {
                    for (String pattern : patterns) {
                        operations.keys(pattern);
                    }
                }
        );
        return allMatchedKeys(keys);
    }

    /**
     * 展开所有匹配到的 key
     *
     * @param keys 匹配到的key 集合 List<Set<>>
     * @return 匹配到的所有key
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Set<String> allMatchedKeys(List<Object> keys) {
        if (CollUtil.isEmpty(keys)) {
            return Set.of();
        }
        return (Set) keys.stream()
                .filter(key -> key instanceof Collection)
                .flatMap(key -> ((Collection) key).stream())
                .collect(Collectors.toSet());
    }

    /**
     * scan 模式获取所有 key集合
     *
     * @param keyPatterns key匹配表达式集合
     * @return key集合
     */
    public static Set<String> scan(Set<String> keyPatterns) {
        if (CollUtil.isEmpty(keyPatterns)) {
            return Set.of();
        }
        return scan(1000, keyPatterns);
    }


    /**
     * scan 模式获取所有 key集合
     *
     * @param scanSize    每次扫描数量
     * @param keyPatterns key匹配表达式集合
     * @return key集合
     */
    public static Set<String> scan(int scanSize, Set<String> keyPatterns) {
        Object value = redisTemplate.execute(
                LazyLoadKeysMatchScript.KEYS_MATCH_SCRIPT,
                LazyRedisSerializer.KEY_SERIALIZER,
                LazyRedisSerializer.KEY_SERIALIZER,
                List.copyOf(keyPatterns),
                String.valueOf(scanSize)
        );
        if (value == null) {
            return Set.of();
        }
        return toBean(value, new TypeReference<>() {
        });
    }

    /**
     * 匹配 key 并删除
     *
     * @param keyPatterns key 匹配表达式集合
     */
    public static void matchThenDelete(Set<String> keyPatterns) {
        redisTemplate.execute(
                LazyLoadKeysMatchDeleteScript.KEYS_MATCH_DELETE_SCRIPT,
                List.copyOf(keyPatterns),
                1000
        );
    }

    /**
     * 生成 14 位String 编号/单号
     */
    public static String noStr(String key) {
        String format = LocalDate.now().format(RedisUtilConstant.NO_FORMATTER);
        String redisKey = RedisUtil.key(RedisConstant.NO_KEY, format, key);
        Long increment = RedisUtil.getRedisTemplate().opsForValue().increment(redisKey);
        if (increment == null) {
            throw new RuntimeException("redis increment operate error");
        }
        if (increment == 1) {
            RedisUtil.expire(redisKey, 1, TimeUnit.DAYS);
        }
        return format + String.format("%08d", increment);
    }

    /**
     * 生成 head +{14位String}编号/单号
     * 如 NO22012000000002
     */
    public static String no(String head, String key) {
        return head + noStr(key);
    }

    /**
     * 生成 14 位 Long 编号/单号 如生成订单号可用 RedisUtil.no("order");
     * 237338581361
     */
    public static Long no(String key) {
        return Long.valueOf(noStr(key));
    }

    /**
     * object -> bean 类型转换
     *
     * @param value     值
     * @param reference 类型
     * @param <T>       bean type
     * @return bean
     */
    public static <T> T toBean(Object value, TypeReference<T> reference) {
        return FastJson2.convert(value, reference, LazyRedisSerializer.FAST_JSON_CONFIG.getReaderFeatures());
    }

    /**
     * object -> bean 类型转换
     *
     * @param value 值
     * @param type  类型
     * @param <T>   bean type
     * @return bean
     */
    public static <T> T toBean(Object value, Class<T> type) {
        return FastJson2.convert(value, type, LazyRedisSerializer.FAST_JSON_CONFIG.getReaderFeatures());
    }

    /**
     * 获取分布式锁
     *
     * @param lockName 锁名称
     * @param key      锁 key
     * @return 目标分布式锁
     */
    public static String getLockKey(String lockName, Object key) {
        return RedisUtil.getLockKey(lockName) + (StrUtil.isEmptyIfStr(key) ? "" : (StrPool.COLON + key));
    }


    // ********** redisson **********

    /**
     * 获取分布式锁
     *
     * @param lockName 锁名称
     * @return 目标分布式锁
     */
    public static String getLockKey(String lockName) {
        return RedisUtil.key(
                applicationName,
                lockName
        );
    }

    /**
     * 锁定并执行
     *
     * @param lockKey 分布式锁
     * @param task    执行任务
     */
    public static void lockRun(String lockKey, Runnable task) {
        RedisUtil.lockRun(lockKey, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 锁定并执行
     *
     * @param lockKey 分布式锁
     * @param task    执行任务
     * @param <T>     返回值类型
     * @return 返回值
     */
    public static <T> T lockRun(String lockKey, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 当 key 对应的 hash值存在时 修改hash 内容，不存在不做操作
     *
     * @param key          key
     * @param hashValueMap 新hash值
     */
    public static void putIfPresent(String key, Map<String, Object> hashValueMap) {
        batchPutIfPresent(
                Map.of(
                        key, hashValueMap
                )
        );
    }

    /**
     * 批量修改hash值 当 key 对应的 hash值存在时 修改hash 内容，不存在不做操作
     *
     * @param keyWithHashValueMap key 与 hash&值的map
     */
    public static void batchPutIfPresent(Map<String, Map<String, Object>> keyWithHashValueMap) {
        if (CollUtil.isEmpty(keyWithHashValueMap)) {
            return;
        }
        List<String> keys = CollUtil.newArrayList();
        List<Object> values = CollUtil.newArrayList();

        //使用迭代器遍历  拼接所有的 key 和 values  
        // 不同的 key 之间使用 RedisConstant.NEXT_KEY_FLAG 分隔
        Iterator<Map.Entry<String, Map<String, Object>>> iterator = keyWithHashValueMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map<String, Object>> current = iterator.next();
            String key = current.getKey();
            Map<String, Object> hashValueMap = current.getValue();
            if (CollUtil.isEmpty(hashValueMap)) {
                continue;
            }
            keys.add(key);
            hashValueMap.forEach(
                    (currKey, value) -> {
                        values.add(currKey);
                        values.add(value);
                    }
            );
            if (iterator.hasNext()) {
                values.add(RedisUtilConstant.NEXT_KEY_FLAG);
            }
        }
        RedisTemplate<String, Object> curRedisTemplate = RedisUtil.getRedisTemplate();
        curRedisTemplate.execute(
                LazyLoadPutIfPresentScript.PUT_IF_PRESENT_SCRIPT,
                keys,
                values.toArray()
        );
    }

    /**
     * 获取redis value 序列化器
     *
     * @return 序列化器
     */
    public static FastJsonRedisSerializer<Object> valueSerializer() {
        return LazyRedisSerializer.VALUE_SERIALIZER;
    }


    /**
     * 初始化
     *
     * @param redisTemplate   redisTemplate
     * @param redissonClient  redissonClient
     * @param applicationName 应用名称
     */
    public static void init(
            RedisTemplate<String, Object> redisTemplate,
            RedissonClient redissonClient,
            String applicationName,
            int doubleDeletionScheduleCorPoolSize
    ) {
        RedisUtil.redisTemplate = redisTemplate;
        RedisUtil.redissonClient = redissonClient;
        RedisUtil.applicationName = applicationName;
        RedisUtil.doubleDeletionScheduleCorPoolSize = doubleDeletionScheduleCorPoolSize;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return RedisUtil.redisTemplate;
    }

    public static RedissonClient getRedissonClient() {
        return RedisUtil.redissonClient;
    }


    /**
     * 工具类常量
     */
    private interface RedisUtilConstant {
        /**
         * 跳到下一个 key 标识
         */
        String NEXT_KEY_FLAG = "__NeXt__KeY__";

        /**
         * 查询分布式锁后缀
         */
        String QUERY_LOCK = "zqueryzGetbLockb";

        /**
         * 编号 日期格式化方式
         */
        DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");


    }

    /**
     * 懒加载 获取 fastjson2 redis 序列化器
     */
    public static class LazyRedisSerializer {

        /**
         * fastjson2 redis 序列化器
         */
        private static final FastJsonRedisSerializer<Object> VALUE_SERIALIZER = new FastJsonRedisSerializer<>(Object.class);
        /**
         * fastjson配置
         */
        private static final FastJsonConfig FAST_JSON_CONFIG = new FastJsonConfig();
        public static RedisSerializer<?> KEY_SERIALIZER = new StringRedisSerializer();
        public static RedisSerializer<?> HASH_KEY_SERIALIZER = new HashKeySerializer();

        static {
            FAST_JSON_CONFIG.setJSONB(false);
            VALUE_SERIALIZER.setFastJsonConfig(FAST_JSON_CONFIG);

        }
    }

    /**
     * 懒加载 获取延迟执行线程池
     */
    private static class DelayExecutor {
        private static final ScheduledExecutorService DELAY_EXECUTOR = new ScheduledThreadPoolExecutor(
                doubleDeletionScheduleCorPoolSize,
                ThreadFactoryBuilder.create()
                        .setNamePrefix("redis")
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 懒加载 获取当 hash key 存在时 put 的lua 脚本
     */
    private static class LazyLoadPutIfPresentScript {
        private static final RedisScript<Void> PUT_IF_PRESENT_SCRIPT = RedisScript.of(new ClassPathResource("lua/batch_put_if_present.lua"));
    }


    /**
     * 批量匹配所有 key
     */
    private static class LazyLoadKeysMatchScript {

        private static final RedisScript<Set> KEYS_MATCH_SCRIPT = RedisScript.of(new ClassPathResource("lua/keys_match.lua"), Set.class);
    }

    /**
     * 匹配所有 key 并删除
     */
    private static class LazyLoadKeysMatchDeleteScript {

        private static final RedisScript<Void> KEYS_MATCH_DELETE_SCRIPT = RedisScript.of(new ClassPathResource("lua/keys_match_delete.lua"));
    }
}
