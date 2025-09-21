package com.medusa.gruul.addon.rebate.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.model.enums.ChangeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 返利操作类型 枚举
 */
@Getter
@RequiredArgsConstructor
public enum RebateType {

    /**
     * 收入
     */
    INCOME(0, ChangeType.INCREASE),
    /**
     * 支出
     */
    EXPENDITURE(1, ChangeType.REDUCE),

    /**
     * 退款收入
     */
    REFUND(2, ChangeType.INCREASE),

    /**
     * 提现
     */
    WITHDRAW(3, ChangeType.REDUCE),
    ;

    @EnumValue
    private final Integer value;

    private final ChangeType changeType;
}
