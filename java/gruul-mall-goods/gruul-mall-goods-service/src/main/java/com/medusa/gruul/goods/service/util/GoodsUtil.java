package com.medusa.gruul.goods.service.util;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.constant.GoodsConstant;

/**
 * @author 张治保 date 2022/5/18
 */
public final class GoodsUtil {

    private GoodsUtil() {
    }

    /**
     * 获取缓存 商品缓存 key
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return key
     */
    public static String productCacheKey(Long shopId, Long productId) {
        return RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, productId);
    }

    /**
     * 获取商品类目的缓存
     *
     * @param shopId
     * @param categoryId
     * @return
     */
    public static String productCategoryCacheKey(Long shopId, Long categoryId) {
        return RedisUtil.key(GoodsConstant.CATEGORY_DETAIL_CACHE_KEY, shopId, categoryId);
    }

    /**
     * @param shopId 店铺id
     * @return key
     */
    public static String productCacheKeyPattern(Long shopId) {
        return RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, "*");
    }


    /**
     * 生成redisson分布式锁key
     *
     * @param key shopId 店铺id | productId 商品id | skuId | activityType 活动类型 | activityId   活动id
     * @return lock key
     */
    public static String generateRedissonLockKey(ShopProductSkuKey key) {
        return RedisUtil.key(GoodsConstant.CACHE_KEY_PRODUCT_STORAGE, key.getShopId(), key.getProductId(), "lock");
    }

    public static String userFollowShopKey(Long userId) {
        return RedisUtil.key(String.format(GoodsConstant.USER_FOLLOW_SHOP_KEY, userId));
    }
}
