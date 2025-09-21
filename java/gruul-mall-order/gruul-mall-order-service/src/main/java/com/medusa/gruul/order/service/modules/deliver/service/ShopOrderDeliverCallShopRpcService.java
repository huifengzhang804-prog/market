package com.medusa.gruul.order.service.modules.deliver.service;

import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;

import java.util.List;

/**
 * @author 张治保
 * date 2022/7/26
 */
public interface ShopOrderDeliverCallShopRpcService {

    /**
     * 根据订单号查询店铺订单包裹列表
     *
     * @param deliveryMatch 订单匹配条件
     * @return 店铺订单包裹列表
     */
    List<ShopOrderPackage> deliveredPackages(DeliveryQueryBO deliveryMatch);
}
