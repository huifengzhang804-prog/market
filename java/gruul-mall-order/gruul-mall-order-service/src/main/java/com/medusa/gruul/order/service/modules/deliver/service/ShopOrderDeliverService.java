package com.medusa.gruul.order.service.modules.deliver.service;

import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrdersDeliveryDTO;
import com.medusa.gruul.order.service.model.vo.ShopOrderUndeliveredVO;
import io.vavr.control.Option;

import java.util.List;

/**
 * @author 张治保
 * date 2022/7/26
 */
public interface ShopOrderDeliverService {

    /**
     * 查询未发货商品 列表
     *
     * @param deliveryMatch 订单匹配条件
     * @return 收货人信息与未发货订单列表
     */
    ShopOrderUndeliveredVO undelivered(DeliveryQueryBO deliveryMatch);


    /**
     * 查询所有已发货商品 列表
     *
     * @param deliveryMatch 订单匹配条件
     * @return 收货人信息与未发货订单列表
     */
    List<ShopOrderItem> delivered(DeliveryQueryBO deliveryMatch);

    /**
     * 根据订单号和店铺订单号查询已发货的第一个包裹
     *
     * @param orderNo     订单号
     * @param shopOrderNo 店铺订单号
     * @param packageId   包裹id
     * @return 第一个店铺订单包裹
     */
    Option<ShopOrderPackage> deliveredPackageFirst(String orderNo, String shopOrderNo, Long packageId);

    /**
     * 订单发货
     *
     * @param deliveryMatch 订单匹配条件
     * @param orderDelivery 订单发货信息
     */
    void deliver(DeliveryQueryBO deliveryMatch, OrderDeliveryDTO orderDelivery);

    /**
     * 批量发货
     *
     * @param deliveryMatch    订单匹配条件
     * @param ordersDeliveries 订单发货信息列表
     */
    void batchDeliver(DeliveryQueryBO deliveryMatch, List<OrdersDeliveryDTO> ordersDeliveries);
}
