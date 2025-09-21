package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>发票抬头所属方类型</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceHeaderOwnerTypeEnum {

    /**
     * C端用户
     */
    USER(1),

    /**
     * 店铺
     */
    SHOP(2);

    @EnumValue
    private final int value;
}
