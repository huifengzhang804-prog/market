package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 *  交易状态
 *
 *
 * @author xiaoq
 * @ Description TradeStatus.java
 * @date 2022-07-25 14:18
 */
@Getter
@RequiredArgsConstructor
public enum TradeStatus {
    /**
     * 1（交易创建，等待买家付款）
     */
    PENDING_PAYMENT(1),

    /**
     *  2（未付款交易超时关闭）
     */
    OVERTIME_CLOSE(2),

    /**
     *  3（交易支付成功)
     */
    SUCCESS_PAYMENT(3),
    ;

    @EnumValue
    private final Integer tradeStatus;
}
