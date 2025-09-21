package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 交易类型枚举
 *
 * @author xiaoq
 * @Description DealType.java
 * @date 2022-08-31 09:43
 */
@Getter
@RequiredArgsConstructor
public enum DealType implements Serializable {


    /**
     * 系统赠送
     */
    SYSTEM_GIVE(1),

    /**
     * 个人充值
     */
    PERSONAL_CHARGING(2),

    /**
     * 系统充值
     */
    SYSTEM_CHARGING(3),

    /**
     * 购物消费
     */
    SHOPPING_PURCHASE(4),

    /**
     * 购买会员
     */
    PURCHASE_MEMBER(5),

    /**
     * 退款成功
     */
    REFUND_SUCCEED(6),

    /**
     * 提现
     */
    WITHDRAW(7),

    /**
     * 赠送
     */
    GIVE(8);


    @EnumValue
    private final Integer type;
}
