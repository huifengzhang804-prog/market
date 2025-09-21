package com.medusa.gruul.addon.ic.modules.ic.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@RequiredArgsConstructor
public enum ICDeliveryType {
    /**
     * 商家配送
     */
    SELF(1),

    /**
     * uupt 配送
     */
    UUPT(2),
    ;
    @EnumValue
    private final int value;
}
