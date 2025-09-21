package com.medusa.gruul.carrier.pigeon.service.im.service;

import com.medusa.gruul.carrier.pigeon.service.im.entity.ShopInfo;

/**
 * <p>店铺接口</p>
 * @author An.Yan
 */
public interface IShopService {
    /**
     * 尝试从Redis中获取店铺信息,若不存在则从RPC拉取并缓存到Redis.
     * @param shopId 店铺ID
     * @return {@link ShopInfo}
     */
    ShopInfo checkAndGetMessageShop(Long shopId);
}
