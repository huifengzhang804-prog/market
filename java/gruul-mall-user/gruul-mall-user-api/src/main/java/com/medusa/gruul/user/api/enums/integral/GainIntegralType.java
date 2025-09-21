package com.medusa.gruul.user.api.enums.integral;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 获取积分类型
 *
 * @author xiaoq
 * @Description 获取积分类型
 * @date 2023-02-01 16:11
 */
@Getter
@RequiredArgsConstructor
public enum GainIntegralType {
    /**
     * 每日登入
     */
    DAY_LOGIN(1, true),

    /**
     * 积分商品兑换
     */
    INTEGRAL_PRODUCT_EXCHANGE(2, false),

    /**
     * 每日分享
     */
    DAY_SHARE(3, true),

    /**
     * 积分清空
     */
    INTEGRAL_CLEAR(4, false),

    /**
     * 每日签到
     */
    DAY_SIGN_IN(5, true),

    /**
     * 系统充值
     */
    SYSTEM_RECHARGE(6, false),

    /**
     * 系统扣除
     */
    SYSTEM_DEDUCT(7, false),

    /**
     * 订单消费
     */
    ORDER_CONSUMPTION(8, false),

    /**
     * 订单取消
     */
    ORDER_CANCEL(9,false),
    /**
     * 积分消费所得
     */
    INTEGRAL_CONSUME(10,true)
    ;
    @EnumValue
    private final Integer value;

    /**
     * 是否翻倍
     */
    @EnumValue
    private final Boolean isDouble;
}

