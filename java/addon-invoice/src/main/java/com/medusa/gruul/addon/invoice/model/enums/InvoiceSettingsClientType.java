package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceSettingsClientType {


    /**
     * 商家端
     */
    SHOP(1),

    /**
     * 供应商端
     */
    SUPPLIER(2),;

    @EnumValue
    private final Integer value;
}
