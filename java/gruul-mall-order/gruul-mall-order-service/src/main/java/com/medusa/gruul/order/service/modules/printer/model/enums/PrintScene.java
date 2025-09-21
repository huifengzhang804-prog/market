package com.medusa.gruul.order.service.modules.printer.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Getter
@RequiredArgsConstructor
public enum PrintScene {
    /**
     * 手动打印  不自动触发
     */
    MANUAL(1),

    /**
     * 付款后自动打印
     */
    AUTO_PAID(2),

    /**
     * 发货后自动打印
     */
    AUTO_DELIVERY(3);

    @EnumValue
    private final int value;
}
