package com.medusa.gruul.shop.service.model;

import static com.medusa.gruul.shop.api.constant.ShopConstant.SHOP_CACHE_KEY_PREFIX;

/**
 * @author 张治保
 * date 2022/11/28
 */
public interface ShopConstants {

    /**
     * 店铺信息缓存key
     * key => gruul:mall:shop:info
     * hash key => shopId
     */
    String SHOP_BASE_INFO = SHOP_CACHE_KEY_PREFIX + ":base:info";

    /**
     * 店铺地址 和当前请求ip地址的 距离
     */
    String SHOP_GD_MAP = SHOP_CACHE_KEY_PREFIX + ":gd";

    /**
     * 更新店铺分布式锁
     */
    String SHOP_UPDATE_LOCK = SHOP_CACHE_KEY_PREFIX + ":update:lock";

    /**
     * 平台发货配置缓存 key
     */
    String PLATFORM_DELIVER_MODE_SETTINGS = SHOP_CACHE_KEY_PREFIX + ":platform:deliver:mode:settings";

}
