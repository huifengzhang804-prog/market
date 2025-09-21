package com.medusa.gruul.addon.integral.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单状态
 *
 * @author xiaoq
 * @Description 积分订单状态
 * @date 2023-01-31 13:49
 */
@Getter
@RequiredArgsConstructor
public enum IntegralOrderStatus {
    /**
     * 未支付
     */
    UNPAID(1),

    /**
     * 待发货
     */
    PAID(2),

    /**
     * 待收货
     */
    ON_DELIVERY(3),

    /**
     * 已完成
     */
    ACCOMPLISH(4),

    /**
     * 系统关闭
     */
    SYSTEM_CLOSED(5);

    @EnumValue
    private final Integer value;

}
