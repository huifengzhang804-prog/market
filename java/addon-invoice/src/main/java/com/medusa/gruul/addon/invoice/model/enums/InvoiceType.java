package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 发票类型
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceType {

    /**
     * 增值税电子普通发票
     */
    VAT_GENERAL(1),

    /**
     * 增值税电子专用发票
     */
    VAT_SPECIAL(2),;

    @EnumValue
    private final Integer value;
}
