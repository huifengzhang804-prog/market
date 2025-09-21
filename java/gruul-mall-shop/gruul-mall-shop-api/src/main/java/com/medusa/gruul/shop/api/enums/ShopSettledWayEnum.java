package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 店铺入驻方式枚举
 *
 * @author jipeng
 * @since 2025/2/25
 */
@Getter
@RequiredArgsConstructor
public enum ShopSettledWayEnum {

    APPLY(0, "申请入驻"),
    ADD(1, "添加入驻");
    @EnumValue
    private final int value;
    private final String desc;
}
