package com.medusa.gruul.order.service.modules.printer.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@RequiredArgsConstructor
public enum PrintMode {
    /**
     * 同城
     */
    INTRA_CITY(1),

    /**
     * 门店自提
     */
    STORE_PICKUP_SELF(2);

    @EnumValue
    private final int value;
}
