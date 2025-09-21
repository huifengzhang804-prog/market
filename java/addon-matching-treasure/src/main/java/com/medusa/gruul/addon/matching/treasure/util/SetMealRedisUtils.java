package com.medusa.gruul.addon.matching.treasure.util;

import com.medusa.gruul.addon.matching.treasure.constant.MatchingTreasureConstant;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

public interface SetMealRedisUtils {


    RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
    RedissonClient redissonClient = RedisUtil.getRedissonClient();

    /**
     * 获取缓存中的套餐信息
     *
     * @param setMealService 套餐服务
     * @param shopId         店铺id
     * @return 是否存在套餐
     */
    static boolean getCacheValue(ISetMealService setMealService, Long shopId) {
        String key = RedisUtil.key(MatchingTreasureConstant.SET_MEAL_EXIST_KEY, shopId);
        boolean exist = false;
        Integer obj = (Integer) redisTemplate.opsForValue().get(key);
        if (obj != null) {
            return obj > CommonPool.NUMBER_ZERO;
        }
        RLock lock = redissonClient.getLock(MatchingTreasureConstant.SET_MEAL_EXIST_REDIS_LOCK_KEY + shopId.toString());
        lock.lock();
        try {
            obj = (Integer) redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return obj > CommonPool.NUMBER_ZERO;
            }
            LocalDateTime now = LocalDateTime.now();
            SetMeal setMeal = setMealService.query()
                    .select("MIN(end_time) as end_time")
                    .eq("shop_id", shopId)
                    .le("start_time", now)
                    .ge("end_time", now)
                    .ne("set_meal_status", SetMealStatus.ILLEGAL_SELL_OFF)
                    .one();
            long expire = 3600;
            if (setMeal != null) {
                exist = true;
                expire = Duration.between(now, setMeal.getEndTime()).getSeconds();
            } else {
                setMeal = setMealService.query()
                        .select("MIN(start_time) AS start_time")
                        .eq("shop_id", shopId)
                        .gt("start_time", now)
                        .ne("set_meal_status", SetMealStatus.ILLEGAL_SELL_OFF)
                        .one();
                if (setMeal != null) {
                    exist = true;
                    expire = Duration.between(now, setMeal.getStartTime()).getSeconds();
                }
            }
            redisTemplate.opsForValue().set(key, exist ? expire : 0,exist? Duration.ofSeconds(expire):Duration.ofSeconds(300));
            return exist;

        } finally {
            lock.unlock();
        }

    }
}
