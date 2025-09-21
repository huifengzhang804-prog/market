package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.dto.ShopStoreOrderExtraDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;

/**
 *
 *  店铺订单 保存 控制层
 *
 * @author xiaoq
 * @Description 店铺订单 生成 控制层
 * @date 2023-03-19 12:55
 */
public interface SaveShopStoreOrderService {
    /**
     * 门店订单信息保存
     *
     * @param orderPaidBroadcast 订单支付信息
     * @param shopStoreOrderExtra 店铺门店订单额外数据
     */
    void saveStoreOrder(OrderPaidBroadcastDTO orderPaidBroadcast,  ShopStoreOrderExtraDTO shopStoreOrderExtra);

}
