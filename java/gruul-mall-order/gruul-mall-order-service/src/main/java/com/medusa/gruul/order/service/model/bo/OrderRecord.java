package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.order.api.entity.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/4/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderRecord implements Serializable {
    private Order order;
    private OrderReceiver receiver;
    private OrderPayment payment;
    private List<OrderDiscount> discounts;
    private List<OrderDiscountItem> discountItems;
    private List<ShopOrder> shopOrders;
    private List<ShopOrderItem> shopOrderItems;
}
