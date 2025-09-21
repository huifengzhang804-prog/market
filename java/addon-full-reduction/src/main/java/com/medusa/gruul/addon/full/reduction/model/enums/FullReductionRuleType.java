package com.medusa.gruul.addon.full.reduction.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.RequiredArgsConstructor;


/**
 * 满减类型
 *
 * @author wudi
 */
@RequiredArgsConstructor
public enum FullReductionRuleType {


    /**
     * 满减
     */
    FULL_REDUCTION(0),

    /**
     * 满折
     */
    FULL_DISCOUNT(1);

    @EnumValue
    private final Integer value;
}
