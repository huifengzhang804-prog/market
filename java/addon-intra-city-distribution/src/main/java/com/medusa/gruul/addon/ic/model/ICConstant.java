package com.medusa.gruul.addon.ic.model;

/**
 * @author 张治保
 * @since 2024/8/13
 */
public interface ICConstant {

    /**
     * 当前服务缓存前缀
     */
    String CACHE_KEY_PREFIX = "addon:intra-city";

    /**
     * 店铺同城配置 缓存 key
     */
    String SHOP_CONFIG_CACHE_KEY = CACHE_KEY_PREFIX + ":shop:config";

    /**
     * 店铺同城配置 分布式锁
     */
    String SHOP_CONFIG_CACHE_SAVE_LOCK_KEY = SHOP_CONFIG_CACHE_KEY + "lock";


    String SHOP_PICKUP_CODE_KEY = CACHE_KEY_PREFIX + ":shop:pickup:code";

    /**
     * 店铺配送单分布式锁
     */
    String SHOP_ORDER_TAKE_LOCK_KEY = CACHE_KEY_PREFIX + "shop:order:takLock";

    /**
     * 店铺配送员位置信息缓存 key
     */
    String SHOP_ORDER_COURIER_KEY = CACHE_KEY_PREFIX + "shop:order:courier";
}
