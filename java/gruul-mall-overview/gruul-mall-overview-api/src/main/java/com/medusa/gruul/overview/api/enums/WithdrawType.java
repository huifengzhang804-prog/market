package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.model.enums.PayType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 提现支付类型
 *
 * @author 张治保
 * date 2022/11/19
 */
@Getter
@RequiredArgsConstructor
public enum WithdrawType {

    /**
     * 微信
     */
    WECHAT(1, PayType.WECHAT),

    /**
     * 消费者
     */
    ALIPAY(2, PayType.ALIPAY),

    /**
     * 银行卡
     */
    BANK_CARD(3, null);

    @EnumValue
    private final Integer value;

    /**
     * 提现线上支付类型
     */
    private final PayType payType;
}
