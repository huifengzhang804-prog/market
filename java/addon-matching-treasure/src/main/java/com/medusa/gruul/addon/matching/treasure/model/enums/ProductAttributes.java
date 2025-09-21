package com.medusa.gruul.addon.matching.treasure.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductAttributes {

    /**
     * 主商品
     */
    MAIN_PRODUCT(0),
    /**
     * 搭配商品
     */
    MATCHING_PRODUCTS(1);


    @EnumValue
    private final Integer value;
}
