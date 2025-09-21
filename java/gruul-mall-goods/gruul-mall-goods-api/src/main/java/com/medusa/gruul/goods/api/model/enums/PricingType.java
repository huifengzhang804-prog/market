package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2023/8/8
 * @describe 设价方式
 */
@Getter
@RequiredArgsConstructor
public enum PricingType {
    /**
     * 比例
     */
    RATE(1),
    /**
     * 固定
     */
    REGULAR(2),

    ;
    @EnumValue
    private final int value;
}
