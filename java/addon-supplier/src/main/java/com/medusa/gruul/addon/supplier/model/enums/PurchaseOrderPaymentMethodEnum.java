package com.medusa.gruul.addon.supplier.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 采购订单付款方式枚举
 * @author jipeng
 * @since 2025/3/1
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderPaymentMethodEnum {
    SHOP_BALANCE(1,"店铺余额"),
    PAY_OFF_LINE(2,"线下打款"),
    ;
    private final Integer code ;
    private final String msg;
}
