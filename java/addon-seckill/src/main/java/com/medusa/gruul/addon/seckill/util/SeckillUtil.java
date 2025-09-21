package com.medusa.gruul.addon.seckill.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.seckill.model.SeckillConst;
import com.medusa.gruul.addon.seckill.model.enums.SeckillStatus;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillActivityDao;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillProductDao;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillActivity;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillProduct;
import com.medusa.gruul.common.model.activity.ActivityTimeAndPrice;
import com.medusa.gruul.common.model.activity.SimpleActivity;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.o.RangeDateTime;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/5/28
 */
public interface SeckillUtil {

    /**
     * 一天的小时数
     */
    int ONE_DAY_HOUR = 24;

    /**
     * 时区 直接使用中国固定时区
     */
    ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    /**
     * 当前时间距结束时间最小的分钟数n 应设置位 3-60 之间的值
     */
    int NOW_TO_END_MIN_MINUTES = 5;

    /**
     * 获取活动时间范围
     *
     * @param date     活动日期
     * @param round    活动场次
     * @param duration 每场活动的持续时间
     * @return 活动时间范围
     */
    static Tuple2<LocalDateTime, LocalDateTime> timeRange(LocalDate date, int round, int duration) {
        LocalDateTime startTime = startTime(date, round, duration);
        return Tuple.of(
                startTime,
                startTime.plusHours(duration)
                        .minusNanos(1)
        );
    }

    /**
     * 获取活动开始时间
     *
     * @param date     活动日期
     * @param round    活动场次
     * @param duration 每场活动的持续时间
     * @return 活动开始时间
     */
    static LocalDateTime startTime(LocalDate date, int round, int duration) {
        return date.atTime(LocalTime.of(round * duration, 0));
    }

    /**
     * 获取活动结束时间
     *
     * @param date     活动日期
     * @param round    活动场次
     * @param duration 每场活动的持续时间
     * @return 活动结束时间
     */
    static LocalDateTime endTime(LocalDate date, int round, int duration) {
        return startTime(date, round, duration)
                .plusHours(duration)
                .minusNanos(1);
    }

    /**
     * 是否是不支持的结束时间 如果结束时间距当前时间 小于 三分钟 则视为不支持
     *
     * @param endTime 结束时间
     * @return 是否是不支持的结束时间
     */
    static boolean isWrongEndTime(LocalDateTime endTime) {
        return Duration.between(LocalDateTime.now(), endTime)
                .toMinutes() <= NOW_TO_END_MIN_MINUTES;
    }


    /**
     * 是否是错误的的活动场次
     *
     * @param round    活动场次
     * @param duration 每场活动的持续时间
     * @return 是否是错误的的活动场次
     */
    static boolean isWrongRound(int round, int duration) {
        return round * duration > ONE_DAY_HOUR;
    }

    /**
     * 根据活动开始时间 判断该活动是否可以直接缓存
     *
     * @return 是否可以直接缓存
     */
    static boolean cacheNow(LocalDateTime startTime) {
        return isWrongEndTime(startTime);
    }


    /**
     * time 转时间戳
     *
     * @param time local date time
     * @return 时间戳
     */
    static long timeToScore(LocalDateTime time) {
        return time.atZone(ZONE_ID).toInstant().toEpochMilli();
    }


    /**
     * 获取当前时间所处的场次
     *
     * @param time     当前时间
     * @param duration 每场活动的持续时间
     * @return 当前场次
     */
    static int round(LocalDateTime time, int duration) {
        return time.getHour() / duration;
    }

    /**
     * 获取每天可用的场次
     *
     * @param duration 每场活动的持续时间
     * @return 场次
     */
    static int rounds(int duration) {
        return ONE_DAY_HOUR / duration;
    }


    /**
     * 获取活动时间范围 与价格
     *
     * @param shopId      店铺id
     * @param activityId  活动id
     * @param productId   商品id
     * @param activityDao 活动dao
     * @param productDao  商品dao
     * @return 活动时间范围 与价格
     */
    @SuppressWarnings("unchecked")
    static ActivityTimeAndPrice getTimeAndPrice(
            Long shopId,
            Long activityId,
            Long productId,
            SeckillActivityDao activityDao,
            SeckillProductDao productDao
    ) {
        //活动缓存 ke
        String activityCacheKey = activityKey(shopId, activityId);
        //活动商品缓存 key
        String productsKey = activityProductsKey(shopId, activityId);
        ActivityTimeAndPrice result = new ActivityTimeAndPrice();
        RedisUtil.getCache(
                () -> {
                    List<Object> objects = RedisUtil.executePipelined(
                            redisTemplate -> {
                                redisTemplate.opsForHash().entries(activityCacheKey);
                                redisTemplate.opsForHash().get(productsKey, productId);
                            }
                    );
                    Map<String, Object> activityEntries = (Map<String, Object>) objects.get(0);
                    SimpleActivity activity = MapUtil.isEmpty(activityEntries) ? null : RedisUtil.toBean(activityEntries, SimpleActivity.class);
                    Map<Long, Long> skuPriceMap = RedisUtil.toBean(objects.get(1), new TypeReference<>() {
                    });
                    result.setActivity(activity).setPrice(skuPriceMap);
                    if (activity == null || MapUtil.isEmpty(skuPriceMap)) {
                        return null;
                    }
                    return result;
                },
                () -> {
                    SimpleActivity simpleActivity = result.getActivity();
                    if (simpleActivity == null) {
                        SeckillActivity activity = activityDao.lambdaQuery()
                                .select(SeckillActivity::getStartTime, SeckillActivity::getEndTime, SeckillActivity::getStackable)
                                .eq(SeckillActivity::getStatus, SeckillStatus.OK)
                                .eq(SeckillActivity::getShopId, shopId)
                                .eq(SeckillActivity::getId, activityId)
                                .one();
                        if (activity == null) {
                            return null;
                        }
                        simpleActivity = new SimpleActivity()
                                .setRange(new RangeDateTime().setStart(activity.getStartTime()).setEnd(activity.getEndTime()))
                                .setStackable(activity.getStackable());
                        result.setActivity(simpleActivity);
                    }

                    List<SeckillProduct> skus = productDao.lambdaQuery()
                            .select(SeckillProduct::getSkuId, SeckillProduct::getPrice)
                            .eq(SeckillProduct::getShopId, shopId)
                            .eq(SeckillProduct::getActivityId, activityId)
                            .eq(SeckillProduct::getProductId, productId)
                            .list();
                    if (CollUtil.isEmpty(skus)) {
                        return null;
                    }
                    return result
                            .setPrice(
                                    skus.stream()
                                            .collect(Collectors.toMap(SeckillProduct::getSkuId, SeckillProduct::getPrice))
                            );
                },
                timeAndPrice -> {
                    SimpleActivity activity = timeAndPrice.getActivity();
                    Duration expire = Duration.between(LocalDateTime.now(), activity.getRange().getEnd());
                    RedisUtil.executePipelined(
                            redisTemplate -> {
                                HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
                                hashOperations.put(productsKey, productId, timeAndPrice.getPrice());
                                redisTemplate.expire(productsKey, expire);
                                hashOperations.putAll(activityCacheKey, RedisUtil.toBean(activity, Map.class));
                                redisTemplate.expire(activityCacheKey, expire);
                            }
                    );

                },
                productsKey
        );
        return result;
    }


    /**
     * 缓存活动 及对应活动商品
     *
     * @param activity           常用基本的活动配置
     * @param productSkuPriceMap 商品及对应sku价格map
     */
    @SuppressWarnings("unchecked")
    static void cacheActivityProducts(CurrentActivityKey activityKey, SimpleActivity activity, Map<Long, Map<Long, Long>> productSkuPriceMap) {
        Long shopId = activityKey.getShopId();
        Long activityId = activityKey.getActivityId();
        //活动缓存 ke
        String activityCacheKey = activityKey(shopId, activityId);
        //活动商品缓存 key
        String productsKey = activityProductsKey(shopId, activityId);

        //缓存过期时间
        Duration expire = Duration.between(LocalDateTime.now(), activity.getRange().getEnd());
        RedisUtil.executePipelined(
                redisTemplate -> {
                    HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
                    hashOperations.putAll(productsKey, productSkuPriceMap
//                            .entrySet()
//                            .stream()
//                            .collect(Collectors.toMap(
//                                    entry -> String.valueOf(entry.getKey()),
//                                    Map.Entry::getValue
//                            ))
                    );
                    redisTemplate.expire(productsKey, expire);
                    hashOperations.putAll(activityCacheKey, RedisUtil.toBean(activity, Map.class));
                    redisTemplate.expire(activityCacheKey, expire);
                }
        );
    }


    /**
     * 缓存活动key
     *
     * @param range 活动时间范围
     */
    static void cacheRound(RangeDateTime range) {
        long score = timeToScore(range.getStart());
        roundCountIncr(Map.of(score, (long) CommonPool.NUMBER_ONE));
        RedisUtil.getRedisTemplate()
                .opsForZSet()
                .addIfAbsent(SeckillConst.ROUND_CACHE_KEY, range, score);
    }

    private static RLock getCountLock(Long score) {
        return RedisUtil.getRedissonClient().getLock(RedisUtil.key(SeckillConst.ROUND_CACHE_KEY, score + "_lock"));
    }


    /**
     * 场次活动数量增减
     *
     * @param roundCountMap 场次计数map key 场次时间戳 value 场次数量 负数则减少 正数则增加
     */
    @SuppressWarnings("unchecked")
    private static void roundCountIncr(Map<Long, Long> roundCountMap) {
        if (CollUtil.isEmpty(roundCountMap)) {
            return;
        }
        RedissonClient redissonClient = RedisUtil.getRedissonClient();
        RLock[] locks = roundCountMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != 0)
                .map(entry -> getCountLock(entry.getKey()))
                .toArray(RLock[]::new);
        if (ArrayUtil.isEmpty(locks)) {
            return;
        }
        RLock lock = redissonClient.getMultiLock(locks);
        lock.lock();
        List<Map.Entry<Long, Long>> entries = List.copyOf(roundCountMap.entrySet());
        try {
            List<Object> incrementResults = RedisUtil.executePipelined(
                    redisTemplate -> {
                        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                        for (Map.Entry<Long, Long> entry : entries) {
                            valueOperations.increment(
                                    roundCountKey(entry.getKey()),
                                    entry.getValue()
                            );
                        }
                    }
            );
            Set<Long> needClearRoundScores = new HashSet<>(entries.size());
            for (int index = 0; index < entries.size(); index++) {
                Long remain = (Long) incrementResults.get(index);
                if (remain != null && remain > 0) {
                    continue;
                }
                needClearRoundScores.add(entries.get(index).getKey());
            }
            if (CollUtil.isEmpty(needClearRoundScores)) {
                return;
            }
            clearAllOfRound(needClearRoundScores);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除活动轮次缓存信息
     *
     * @param scores 开始时间时间戳 scores
     */
    @SuppressWarnings("unchecked")
    private static void clearAllOfRound(Set<Long> scores) {
        RedisUtil.executePipelined(
                redisTemplate -> {
                    ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
                    scores.forEach(
                            score -> {
                                //删除缓存的场次
                                zSet.removeRangeByScore(SeckillConst.ROUND_CACHE_KEY, score, score);
                                //删除场次活动数量
                                redisTemplate.delete(roundCountKey(score));
                            }
                    );

                }
        );
    }


    /**
     * 清除活动信息
     *
     * @param startTimes   活动开始时间集合 用于删除 活动场次及场次计数缓存
     * @param activityKeys 活动key集合 用于删除活动商品缓存
     */
    @SuppressWarnings("unchecked")
    static void clearAllOfRoundAndProducts(Set<LocalDateTime> startTimes, Set<CurrentActivityKey> activityKeys) {
        if (CollUtil.isEmpty(startTimes)) {
            return;
        }
        //活动商品 key 集合 用于删除数据
        Set<String> removeKeys = activityKeys.stream()
                .map(key -> activityProductsKey(key.getShopId(), key.getActivityId()))
                .collect(Collectors.toSet());
        TreeSet<Long> scores = startTimes
                .stream()
                .map(SeckillUtil::timeToScore)
                .collect(Collectors.toCollection(TreeSet::new));
        //场次计数 key 也要加入到删除缓存key
        scores.forEach(score -> removeKeys.add(roundCountKey(score)));
        RLock[] locks = scores.stream()
                .map(SeckillUtil::getCountLock)
                .toArray(RLock[]::new);
        RLock lock = RedisUtil.getRedissonClient().getMultiLock(locks);
        lock.lock();
        try {
            RedisUtil.executePipelined(
                    redisTemplate -> {
                        redisTemplate.opsForZSet()
                                .removeRangeByScore(
                                        SeckillConst.ROUND_CACHE_KEY,
                                        scores.first(),
                                        scores.last()
                                );
                        redisTemplate.delete(removeKeys);
                    }
            );
        } finally {
            lock.unlock();
        }

        //删除活动商品


    }


    /**
     * 获取场次活动数量缓存key
     *
     * @param score 时间戳 score
     * @return 缓存key
     */
    private static String roundCountKey(long score) {
        return RedisUtil.key(SeckillConst.ROUND_ACTIVITY_COUNT_CACHE_KEY, score);
    }

    /**
     * 获取活动缓存信息 key
     */
    private static String activityKey(Long shopId, Long activityId) {
        return RedisUtil.key(SeckillConst.SECKILL_ACTIVITY_CACHE_KEY, shopId, activityId);
    }


    /**
     * 获取活动商品缓存key
     *
     * @param shopId     店铺id
     * @param activityId 活动id
     * @return 缓存key
     */
    static String activityProductsKey(Long shopId, Long activityId) {
        return RedisUtil.key(SeckillConst.SECKILL_ACTIVITY_PRODUCT_CACHE_KEY, shopId, activityId);
    }


    /**
     * 分页查询活动场次
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分页查询结果
     */
    @SuppressWarnings("unchecked")
    static IPage<RangeDateTime> pageRound(long current, long size) {
        ZSetOperations<String, Object> zSet = RedisUtil.getRedisTemplate().opsForZSet();
        Long roundSize = zSet.size(SeckillConst.ROUND_CACHE_KEY);
        roundSize = roundSize == null ? CommonPool.NUMBER_ZERO : roundSize;
        IPage<RangeDateTime> result = new Page<>(current, size, roundSize);
        if (roundSize == CommonPool.NUMBER_ZERO) {
            return result;
        }
        List<RangeDateTime> times = CollUtil.emptyIfNull(
                        zSet.range(SeckillConst.ROUND_CACHE_KEY, result.offset(), result.offset() + size)
                )
                .stream()
                .map(value -> RedisUtil.toBean(value, RangeDateTime.class))
                .toList();
        //根据当前时间判断是否有过期数据 如果有过期数据则删除过期的数据
        LocalDateTime now = LocalDateTime.now();
        //过期的活动
        Object[] expiredActivities = times.stream()
                .filter(time -> time.getEnd().isBefore(now))
                .toArray(RangeDateTime[]::new);
        //删除过期的活动
        if (ArrayUtil.isEmpty(expiredActivities)) {
            return result.setRecords(times);
        }
        //移除过期数据
        RedisUtil.executePipelined(
                redisTemplate -> {
                    //删除过期的场次
                    redisTemplate.opsForZSet().remove(SeckillConst.ROUND_CACHE_KEY, expiredActivities);
                    //删除过期的场次计数器
                    Set<String> expiredCountKeys = Arrays.stream(expiredActivities)
                            .map(time -> roundCountKey(timeToScore(((RangeDateTime) time).getStart())))
                            .collect(Collectors.toSet());
                    RedisUtil.delete(expiredCountKeys);
                }
        );
        return pageRound(current, size);
    }


    /**
     * 删除对应的店铺活动
     */
    static void clearShopActivityCache(Long shopId, Set<Long> activityIds) {
        if (CollUtil.isEmpty(activityIds)) {
            return;
        }
        //获取所有需要删除的缓存 key
        Set<String> removeKeys = CollUtil.newHashSet();
        activityIds.forEach(activityId -> {
            removeKeys.add(activityKey(shopId, activityId));
            removeKeys.add(activityProductsKey(shopId, activityId));
        });
        RedisUtil.delete(removeKeys);
    }

    /**
     * 清除活动场次缓存 减少场次计数 如果场次计数为0 则删除场次缓存
     *
     * @param roundCountMap 场次计数map
     */
    static void clearActivityRoundsCache(Map<LocalDateTime, Long> roundCountMap) {
        if (MapUtil.isEmpty(roundCountMap)) {
            return;
        }
        roundCountIncr(
                roundCountMap.entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> timeToScore(entry.getKey()),
                                        entry -> -entry.getValue()
                                )
                        )
        );

    }


}
