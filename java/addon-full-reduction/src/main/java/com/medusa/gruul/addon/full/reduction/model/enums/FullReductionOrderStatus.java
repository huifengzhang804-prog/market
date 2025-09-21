package com.medusa.gruul.addon.full.reduction.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@RequiredArgsConstructor
public enum FullReductionOrderStatus {
    /**
     * 未支付
     */
    UNPAID(1),

    /**
     * 已支付
     */
    PAID(2),
    ;
    @EnumValue
    private final Integer value;

}
