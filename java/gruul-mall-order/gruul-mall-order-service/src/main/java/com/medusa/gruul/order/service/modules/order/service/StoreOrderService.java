package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;

import java.util.Set;

/**
 * 门店订单服务层
 *
 * @author xiaoq
 * @Description 门店订单服务层
 * @date 2023-03-17 11:12
 */
public interface StoreOrderService {
    /**
     * 批量门店订单发货
     *
     * @param orderNos 订单nos
     * @param storeId 门店id
     */
    void batchStoreOrderDeliver(Set<String> orderNos, Long storeId);

    /**
     * 门店订单(核销|收货)
     *
     * @param orderPackageKey 订单收货信息
     */
    void storeOrderConfirmPackage(OrderPackageKeyDTO orderPackageKey);

}
