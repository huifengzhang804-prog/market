package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>发票抬头类型枚举</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceHeaderTypeEnum {

    /**
     * 个人
     */
    PERSONAL(1),

    /**
     * 企业
     */
    ENTERPRISE(2);

    @EnumValue
    private final Integer value;
}
