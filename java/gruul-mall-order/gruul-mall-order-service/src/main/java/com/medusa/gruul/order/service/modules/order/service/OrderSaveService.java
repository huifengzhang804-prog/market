package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.service.model.dto.OrderRemarkDTO;

import java.util.List;

/**
 * 保存订单信息到数据库 流程服务接口
 *
 * @author 张治保
 * date 2022/6/14
 */
public interface OrderSaveService {

    /**
     * 保存订单信息到数据库
     *
     * @param order 订单信息
     */
    void saveOrder(Order order);

    /**
     * 批量保存订单到数据库
     *
     * @param orders 订单列表
     */
    void saveOrderBatch(List<Order> orders);

    /**
     * 保存收货人到数据库
     *
     * @param orderReceiver 收货人信息
     */
    void saveOrderReceiver(OrderReceiver orderReceiver);

    /**
     * 保存收货人到数据库
     *
     * @param orderReceivers 收货人信息列表
     */
    void saveOrderReceivers(List<OrderReceiver> orderReceivers);

    /**
     * 保存店铺优惠信息
     *
     * @param orderDiscounts 订单优惠信息列表
     */
    void saveOrderDiscounts(List<OrderDiscount> orderDiscounts);

    /**
     * 保存订单优惠项
     *
     * @param orderDiscountItems 订单优惠项信息
     */
    void saveOrderDiscountItems(List<OrderDiscountItem> orderDiscountItems);

    /**
     * 保存店铺订单信息到数据库
     *
     * @param shopOrders 店铺订单信息列表
     */
    void saveShopOrders(List<ShopOrder> shopOrders);


    /**
     * 保存店铺订单商品信息到数据库
     *
     * @param shopOrderItems 店铺订单商品信息列表
     */
    void saveShopOrderItems(List<ShopOrderItem> shopOrderItems);

    /**
     * 保存订单支付信息
     *
     * @param orderPayment 订单支付信息
     */
    void saveOrderPayment(OrderPayment orderPayment);

    /**
     * 保存订单支付信息
     *
     * @param orderPayments 订单支付信息列表
     */
    void saveOrderPayments(List<OrderPayment> orderPayments);

    /**
     * 批量备注
     *
     * @param orderRemark 订单备注参数
     */
    void orderBatchRemark(OrderRemarkDTO orderRemark);
}
