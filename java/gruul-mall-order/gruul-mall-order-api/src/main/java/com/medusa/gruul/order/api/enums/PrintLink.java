package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@RequiredArgsConstructor
public enum PrintLink {
    /**
     * 顾客联
     */
    CUSTOMER(1),
    /**
     * 商家联
     */
    MERCHANT(2),
    /**
     * 后厨联
     */
    KITCHEN(3),
    /**
     * 催单联
     */
    REMIND(4);

    @EnumValue
    private final int value;
}
