package com.medusa.gruul.addon.rebate.model.bo;

import com.medusa.gruul.common.model.enums.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class RebateOrderCreateBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6955789084548688040L;

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

}
