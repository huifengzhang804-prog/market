package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.addon.invoice.constants.InvoiceConstant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>发票抬头所属方类型</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum InvoiceOwnerTypeEnum {

    /**
     * 用户端
     */
    USER(1, InvoiceConstant.USER_INVOICE_HANDLER),

    /**
     * 商家端
     */
    SHOP(2,InvoiceConstant.SHOP_INVOICE_HANDLER);

    @EnumValue
    private final int value;

    private final String name;


}
