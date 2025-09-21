package com.medusa.gruul.addon.supplier.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>今日待办VO封装</p>
 *
 * @author An.Yan
 */
@Accessors(chain = true)
@Getter
@Setter
public class ToDoListVO {

    /**
     * 待付款订单数量
     */
    private Long pendingPaymentOrders;

    /**
     * 待发货订单数量
     */
    private Long pendingDeliveredOrders;

    /**
     * 待收货订单数量
     */
    private Long pendingReceivedOrders;
}
