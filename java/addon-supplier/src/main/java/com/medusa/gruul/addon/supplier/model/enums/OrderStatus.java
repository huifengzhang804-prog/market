package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    /**
     * 未支付
     */
    UNPAID(1, false),

    /**
     * 支付审核中
     */
    PAYMENT_AUDIT(2, false),

    /**
     * 审核未通过 已关闭
     */
    AUDIT_FAIL_CLOSED(3, true),

    /**
     * 已支付
     */
    PAID(4, false),

    /**
     * 超时未支付 系统关闭
     */
    SYSTEM_CLOSED(5, true),

    /**
     * 买家关闭订单
     */
    BUYER_CLOSED(6, true),

    /**
     * 卖家关闭订单
     */
    SELLER_CLOSED(7, true);

    @EnumValue
    private final Integer value;

    private final boolean closed;
}
