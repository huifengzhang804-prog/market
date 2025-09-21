package com.medusa.gruul.addon.full.reduction.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {

    /**
     * 全部商品
     */
    ALL_PRODUCT(0, Boolean.FALSE),

    /**
     * 指定商品
     */
    SPECIFIED_PRODUCT_PARTICIPATE(1, Boolean.TRUE),

    /**
     * 指定商品不参与
     */
    SPECIFIED_PRODUCT_NOT_PARTICIPATE(2, Boolean.TRUE);

    @EnumValue
    private final Integer value;
    /**
     * 是否指定的商品
     */
    private final Boolean isAssigned;
}
