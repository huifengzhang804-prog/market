package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ShopCategory {


    /**
     * 线上店铺
     */
    ONLINE_SHOP(0),

    /**
     * 020店铺
     */
    O2O_SHOP(1);

    @EnumValue
    private final Integer value;

    ShopCategory(Integer value) {
        this.value = value;
    }
}
