package com.medusa.gruul.order.api.model;

import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.api.enums.OrderStatus;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author jipeng
 * @since 2025/2/17
 */
@Data
public class OrderInfoDTO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单状态
     */
    private OrderStatus status;

    private String orderStatusContent;
    /**
     * 支付信息
     */
    private OrderPayment payment;
    /**
     * 折扣
     */
    private List<OrderDiscount> orderDiscounts;
}
