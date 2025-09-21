package com.medusa.gruul.addon.matching.treasure.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SetMealType {

    /**
     * 自选商品套餐
     */
    OPTIONAL_PRODUCT(0,"自选商品套餐"),


    /**
     * 固定组合套餐
     */
    FIXED_COMBINATION(1,"固定搭配");

    @EnumValue
    private final Integer value;

    private final String desc;


}
