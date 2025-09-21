package com.medusa.gruul.addon.coupon.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券生效类型
 *
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@RequiredArgsConstructor
public enum EffectType {

    /**
     * 固定 日期期间
     */
    PERIOD(1, Boolean.TRUE),

    /**
     * 领券立即生效
     */
    IMMEDIATELY(2, Boolean.FALSE);

    @EnumValue
    private final Integer value;

    /**
     * 是否是日期期间
     */
    private final Boolean isPeriod;
}
