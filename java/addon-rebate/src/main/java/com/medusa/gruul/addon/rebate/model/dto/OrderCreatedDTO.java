package com.medusa.gruul.addon.rebate.model.dto;

import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.OrderSourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCreatedDTO  implements Serializable {

    /**
     * 订单来源
     */
    private OrderSourceType source;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单类型
     */
    private OrderType orderType;

    /**
     * 总金额
     */
    private Long payAmount;

    /**
     * 所有店铺订单商品
     */
    private List<ShopOrderItem> shopOrderItems;

}
