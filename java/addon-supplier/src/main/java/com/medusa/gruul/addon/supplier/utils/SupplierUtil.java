package com.medusa.gruul.addon.supplier.utils;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.constant.GoodsConstant;

/**
 * @author jipeng
 * @since 2024/12/4
 */
public class SupplierUtil {

    /**
     * 生成redisson分布式锁key
     *
     * @param key shopId 店铺id | productId 商品id | skuId | activityType 活动类型 | activityId   活动id
     * @return lock key
     */
    public static String generateRedissonLockKey(ShopProductKey key) {
        return RedisUtil.key(GoodsConstant.CACHE_KEY_PRODUCT_STORAGE, key.getShopId(), key.getProductId(), "lock");
    }
}
