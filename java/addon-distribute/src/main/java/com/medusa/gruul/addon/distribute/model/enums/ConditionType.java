package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/11/14
 */
@Getter
@RequiredArgsConstructor
public enum ConditionType {

    /**
     * 审核
     */
    APPLY(1),

    /**
     * 满足消费金额
     */
    CONSUMPTION(2);

    @EnumValue
    private final Integer value;
}
