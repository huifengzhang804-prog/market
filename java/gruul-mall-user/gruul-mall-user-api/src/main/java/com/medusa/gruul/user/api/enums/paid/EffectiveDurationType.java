package com.medusa.gruul.user.api.enums.paid;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 付费会员有效时长枚举
 *
 * @author xiaoq
 * @Description EffectiveDurationType.java
 * @date 2022-11-15 10:00
 */
@Getter
@RequiredArgsConstructor
public enum EffectiveDurationType {

    /**
     * 一个月
     */
    ONE_MONTH(30,"1个月"),

    /**
     * 三个月
     */
    THREE_MONTH(ONE_MONTH.value * 3,"3个月"),

    /**
     * 十二个月
     */
    TWELVE_MONTH(ONE_MONTH.value * 12,"12个月"),


    /**
     * 三年
     */
    THREE_YEAR(ONE_MONTH.value * 36,"3年"),

    /**
     * 五年
     */
    FIVE_YEAR(ONE_MONTH.value * 60,"5年");

    @EnumValue
    private final Integer value;

    private final String msg;
}
