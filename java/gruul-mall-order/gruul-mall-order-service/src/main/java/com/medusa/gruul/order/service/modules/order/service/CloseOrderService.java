package com.medusa.gruul.order.service.modules.order.service;


import com.medusa.gruul.order.api.enums.OrderStatus;

import java.util.Set;

/**
 * 关闭订单
 *
 * @author 张治保
 * date 2022/6/24
 */
public interface CloseOrderService {

	/**
	 * 支付超时取消订单
	 *
	 * @param orderNos      订单号集合
	 * @param currentStatus 订单当前状态
	 * @param targetStatus  订单目标状态
	 */
	void updateOrderStatus(Set<String> orderNos, OrderStatus currentStatus, OrderStatus targetStatus);

	/**
	 * 关闭订单
	 *
	 * @param orderNo 订单号
	 */
	void updateOrderStatus(String orderNo);

	/**
	 * 支付超时关闭订单
	 *
	 * @param orderNo 订单号
	 */
	void closeOrderPaidTimeout(String orderNo);

	/**
	 * 店铺关闭订单
	 *
	 * @param orderNo     订单号
	 * @param shopOrderNo 店铺订单号
	 */
	void shopCloseOrder(String orderNo, String shopOrderNo);

}
