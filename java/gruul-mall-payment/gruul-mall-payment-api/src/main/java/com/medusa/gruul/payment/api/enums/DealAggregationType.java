package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 交易聚合类型
 *
 * @author xiaoq
 * @Description DealAggregationType.java
 * @date 2022-09-28 15:08
 */
@Getter
@RequiredArgsConstructor
public enum DealAggregationType {

    /**
     * 全部
     */
    ALL(0),

    /**
     * 用户消费
     */
    USER_CONSUME(1),


    /**
     * 退款成功
     */
    REFUND_SUCCEED(2),

    /**
     * 充值
     */
    CHARGING(3);

    @EnumValue
    private final Integer type;
}
