package com.medusa.gruul.search.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum BrandStatus {


    /**
     * 下架
     */
    SELL_OFF(0),
    /**
     * 上架
     */
    SELL_ON(1);


    @EnumValue
    private final Integer value;
}
