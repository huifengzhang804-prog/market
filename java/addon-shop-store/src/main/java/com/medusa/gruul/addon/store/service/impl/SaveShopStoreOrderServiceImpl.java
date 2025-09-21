package com.medusa.gruul.addon.store.service.impl;

import com.medusa.gruul.addon.store.model.dto.ShopStoreOrderExtraDTO;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import com.medusa.gruul.addon.store.mp.service.IShopStoreOrderService;
import com.medusa.gruul.addon.store.service.SaveShopStoreOrderService;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 店铺订单生成控制实现层
 *
 * @author xiaoq
 * @Description 店铺订单生成控制实现层
 * @date 2023-03-19 12:55
 */
@Service
@RequiredArgsConstructor
public class SaveShopStoreOrderServiceImpl implements SaveShopStoreOrderService {
    private final IShopStoreOrderService shopStoreOrderService;


    @Override
    public void saveStoreOrder(OrderPaidBroadcastDTO orderPaidBroadcast, ShopStoreOrderExtraDTO shopStoreOrderExtra) {
        shopStoreOrderService.save(
                new ShopStoreOrder()
                        .setOrderNo(orderPaidBroadcast.getOrderNo())
                        .setBuyerId(orderPaidBroadcast.getBuyerId())
                        .setPayAmount(orderPaidBroadcast.getPayAmount())
                        .setShopStoreId(shopStoreOrderExtra.getShopStoreId())
                        .setPickUpTime(shopStoreOrderExtra.getPackUpTime())
                        .setPresentDate(LocalDate.now())
                        .setShopId(orderPaidBroadcast.getShopPayAmounts().get(0).getShopId())
        );
    }
}
