package com.medusa.gruul.addon.bargain.utils;


import com.medusa.gruul.addon.bargain.constant.BargainConstant;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 砍价redis工具类
 *
 * @author wudi
 */
public interface BargainRedisUtils {

    RedissonClient REDISSON_CLIENT = RedisUtil.getRedissonClient();


    /**
     * 已发起的砍价商品sku信息缓存key
     *
     * @param userId     用户id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @return 砍价商品sku信息
     */
    static String getBargainSponsorProductSkuKey(Long userId, Long activityId, Long shopId, Long productId) {
        return RedisUtil.key(BargainConstant.BARGAIN_SPONSOR_PRODUCT_SKU_CACHE_KEY, userId, activityId, shopId, productId);
    }


    /**
     * 获取redis锁
     *
     * @param key           锁key
     * @param cacheSupplier 缓存数据
     * @param supplier      数据库数据
     * @param <T>           泛型
     * @return 数据
     */
    static <T> T getRedisLock(String key,
                              Supplier<T> cacheSupplier,
                              Supplier<T> supplier) {

        T t = cacheSupplier.get();
        if (t != null) {
            return t;
        }
        RLock lock = REDISSON_CLIENT.getLock(key);
        lock.lock();
        try {
            t = cacheSupplier.get();
            if (t != null) {
                return t;
            }
            return supplier.get();
        } finally {
            lock.unlock();
        }

    }

    /**
     * 获取redis锁
     *
     * @param key           锁key
     * @param cacheSupplier 缓存数据
     * @param supplier      数据库数据
     * @param <T>           泛型
     * @return 数据
     */
    static <T> T getRedisLockKey(String key,
                                 Supplier<T> cacheSupplier,
                                 Supplier<T> supplier) {

        T t = cacheSupplier.get();
        if (t != null) {
            return t;
        }
        RLock lock = REDISSON_CLIENT.getLock(key);
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }

    }

    /**
     * 获取砍价活动商品sku
     *
     * @param sponsorId  发起人id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @return 砍价发起人商品sku信息
     */
    static BargainSponsorProductSkuVO getCacheBargainSponsorProductSku(Long sponsorId, Long activityId, Long shopId, Long productId) {
        return RedisUtil.getCacheMap(getBargainSponsorProductSkuKey(sponsorId, activityId, shopId, productId), BargainSponsorProductSkuVO.class);

    }

    /**
     * 发起砍价商品金额缓存key
     *
     * @param sponsorId  发起人id
     * @param activityId 活动id
     * @param shopId     店铺id
     * @param productId  商品id
     * @return 发起砍价商品金额缓存key
     */
    static String getBargainSponsorProductAmountKey(Long sponsorId, Long activityId, Long shopId, Long productId) {
        return RedisUtil.key(BargainConstant.BARGAIN_SPONSOR_PRODUCT_AMOUNT_CACHE_KEY, sponsorId, activityId, shopId, productId);
    }

    /**
     * 帮砍人数缓存key
     *
     * @param activityId     活动id
     * @param bargainOrderId 砍价订单id
     * @return 帮砍人数缓存key
     */
    static String getBargainHelpPeopleKey(Long activityId, Long bargainOrderId) {
        return RedisUtil.key(BargainConstant.BARGAIN_FRIENDS, activityId, bargainOrderId);
    }

    /**
     * 砍价商品sku详情缓存key
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 砍价商品sku详情缓存key
     */
    static String getBargainProductSkuDetailKey(Long shopId, Long productId) {
        return RedisUtil.key(BargainConstant.BARGAIN_PRODUCT_DETAIL_CACHE_KEY, shopId, productId);
    }

    /**
     * 获取砍价缓存
     *
     * @param key          key
     * @param cacheFactory 缓存数据
     * @param supplier     数据库数据
     * @return 数据
     */
    static <T> T getBargainSponsorProductSku(String key,
                                             Supplier<T> cacheFactory,
                                             Supplier<T> supplier) {
        T t = cacheFactory.get();
        if (t != null) {
            return t;
        }
        RLock lock = REDISSON_CLIENT.getLock(key);
        lock.lock();
        try {
            t = cacheFactory.get();
            if (t != null) {
                return t;
            }
            return supplier.get();
        } finally {
            lock.unlock();
        }

    }

    /**
     * 设置砍价活动商品sku信息
     *
     * @param cacheKey          key
     * @param sponsorProductSku 发起人商品sku
     */
    static void setCacheBargainSponsorProductSku(String cacheKey, BargainSponsorProductSkuVO sponsorProductSku) {
        RedisUtil.setCacheMap(cacheKey, sponsorProductSku, Duration.ofHours(CommonPool.NUMBER_ONE));
    }

}
