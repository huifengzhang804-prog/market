package com.medusa.gruul.addon.freight.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description ChoiceConditionPostage.java
 * @date 2022-06-08 16:39
 */
@Getter
@RequiredArgsConstructor
public enum ChoiceConditionPostage {
    /**
     * 不包邮
     */
    NO_FREE_SHIPPING(0),
    /**
     * 指定条件包邮
     */
    CONDITION_FREE_SHIPPING(1),
    ;
    @EnumValue
    private final Integer value;
}
