package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2022/6/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderDetailInfoBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6955789324548688040L;

    /**
     * 订单活动类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 用户id
     */
    private Long buyerId;

    /**
     * 订单id
     */
    private String orderNo;


    /**
     * 所有店铺订单商品
     */
    private List<ShopOrderItem> shopOrderItems;

}
