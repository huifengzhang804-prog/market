package com.medusa.gruul.addon.store.service.impl;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.store.model.dto.ShopStoreOrderExtraDTO;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import com.medusa.gruul.addon.store.mp.service.IShopStoreOrderService;
import com.medusa.gruul.addon.store.service.UpdateShopStoreOrderService;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 店铺门店订单 修改服务实现
 *
 * @author xiaoq
 * @Description 店铺门店订单 修改服务实现
 * @date 2023-03-23 18:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateShopStoreOrderServiceImpl implements UpdateShopStoreOrderService {
    private final IShopStoreOrderService shopStoreOrderService;

    @Override
    public void updateStoreOrder(OrderCompletedDTO orderCompleted, ShopStoreOrderExtraDTO shopStoreOrderExtra) {
        ShopStoreOrder shopStoreOrder = shopStoreOrderService.lambdaQuery()
                .eq(ShopStoreOrder::getShopStoreId, shopStoreOrderExtra.getShopStoreId())
                .eq(ShopStoreOrder::getOrderNo, orderCompleted.getOrderNo())
                .eq(ShopStoreOrder::getBuyerId, orderCompleted.getUserId()).one();
        if (shopStoreOrder == null) {
            return;
        }
        shopStoreOrder.setPackageStatus(PackageStatus.SYSTEM_COMMENTED_COMPLETED);
        shopStoreOrderService.updateById(shopStoreOrder);
    }

    @Override
    public void storeOrderDeliverGoods(OrderPackageKeyDTO orderPackageKey, ShopStoreOrderExtraDTO shopStoreOrderExtra) {
        ShopStoreOrder shopStoreOrder = shopStoreOrderService.lambdaQuery()
                .eq(ShopStoreOrder::getShopStoreId, shopStoreOrderExtra.getShopStoreId())
                .eq(ShopStoreOrder::getOrderNo, orderPackageKey.getOrderNo())
                .eq(ShopStoreOrder::getBuyerId, orderPackageKey.getBuyerId())
                .one();
        if (shopStoreOrder == null) {
            return;
        }
        log.info("门店订单信息: ".concat(shopStoreOrder.toString()));
        shopStoreOrderService.lambdaUpdate()
                .set(ShopStoreOrder::getOrderPackage, JSON.toJSONString(orderPackageKey))
                .eq(ShopStoreOrder::getShopStoreId, shopStoreOrderExtra.getShopStoreId())
                .eq(ShopStoreOrder::getOrderNo, orderPackageKey.getOrderNo())
                .eq(ShopStoreOrder::getBuyerId, orderPackageKey.getBuyerId())
                .update();
    }
}
