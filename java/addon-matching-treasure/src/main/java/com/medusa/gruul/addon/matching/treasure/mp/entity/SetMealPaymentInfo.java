package com.medusa.gruul.addon.matching.treasure.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author WuDi
 * @since 2023-03-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_set_meal_payment_info")
public class SetMealPaymentInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 应收金额
     */
    private Long amountReceivable;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 支付订单数
     */
    private Integer payOrder;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 活动id
     */
    private Long activityId;


    /**
     * 店铺id
     */
    private Long shopId;
}
