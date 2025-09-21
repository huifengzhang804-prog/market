package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.dto.ShopStoreOrderExtraDTO;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;

/**
 * 店铺订单 修改 数据层
 *
 * @author xiaoq
 * @Description 店铺订单 修改 数据层
 * @date 2023-03-23 18:00
 */
public interface UpdateShopStoreOrderService {
    /**
     * 修改门店订单状态
     *
     * @param orderCompleted 订单数据
     * @param shopStoreOrderExtra 门店扩展数据
     */
    void updateStoreOrder(OrderCompletedDTO orderCompleted, ShopStoreOrderExtraDTO shopStoreOrderExtra);

    /**
     * 店铺门店订单发货完成 (备货完成/自动发货)
     *
     * @param orderPackageKey  订单包裹信息
     * @param shopStoreOrderExtra 店铺门店订单附加数据
     */
    void storeOrderDeliverGoods(OrderPackageKeyDTO orderPackageKey, ShopStoreOrderExtraDTO shopStoreOrderExtra);
}
