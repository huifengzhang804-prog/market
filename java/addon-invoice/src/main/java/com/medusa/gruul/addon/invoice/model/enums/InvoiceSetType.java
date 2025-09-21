package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceSetType {

    /**
     * 不提供发票
     */
    NO_INVOICE(0),

    /**
     * 增值税电子普通发票
     */
    VAT_GENERAL(1),

    /**
     * 增值税电子专用发票
     */
    VAT_SPECIAL(2),

    /**
     * 增值税电子专用发票 + 增值税电子普通发票
     */
    VAT_COMBINED(3),;

    @EnumValue
    private final Integer value;

}

