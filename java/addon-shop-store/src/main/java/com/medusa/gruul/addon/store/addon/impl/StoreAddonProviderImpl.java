package com.medusa.gruul.addon.store.addon.impl;

import com.medusa.gruul.addon.store.addon.StoreAddonProvider;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.model.OrderDestination;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/9/5
 */
@AddonProviders
@DubboService
@Service
@RequiredArgsConstructor
public class StoreAddonProviderImpl implements StoreAddonProvider {

    private final IShopStoreService shopStoreService;

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID, methodName = "storeAddress")
    public OrderDestination storeAddress(Long shopId, Long storeId) {
        ShopStore store = shopStoreService.lambdaQuery()
                .select(ShopStore::getStoreName, ShopStore::getFunctionaryPhone, ShopStore::getDetailedAddress)
                .eq(ShopStore::getShopId, shopId)
                .eq(BaseEntity::getId, storeId)
                .one();
        if (store == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        return new OrderDestination()
                .setName(store.getStoreName())
                .setMobile(store.getFunctionaryPhone())
                .setAddress(store.getDetailedAddress());
    }

    //storeAddress
}
