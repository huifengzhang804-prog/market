package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>代销订单统计DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class ConsignmentOrderStatisticDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7201384526029055304L;
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
