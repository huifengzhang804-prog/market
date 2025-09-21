package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Getter
@RequiredArgsConstructor
public enum OrderPayType {
    /**
     * 线下支付
     */
    OFFLINE(1, OrderStatus.PAYMENT_AUDIT),

    /**
     * 余额支付
     */
    BALANCE(2, OrderStatus.PAID),
    ;
    @EnumValue
    private final Integer value;

    private final OrderStatus successStatus;
}
