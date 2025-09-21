package com.medusa.gruul.addon.store.addon;

import com.medusa.gruul.order.api.model.OrderDestination;

/**
 * @author 张治保
 * @since 2024/9/5
 */
public interface StoreAddonProvider {

    /**
     * 获取门店地址
     *
     * @param shopId  店铺 id
     * @param storeId 门店 id
     * @return 门店地址
     */
    OrderDestination storeAddress(Long shopId, Long storeId);
}
