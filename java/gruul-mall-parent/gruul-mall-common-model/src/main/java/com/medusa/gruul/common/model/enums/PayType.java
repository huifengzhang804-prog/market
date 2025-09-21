package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付渠道枚举
 *
 * @author xiaoq
 * @ xdescription 支付渠道枚举
 * @date 2022-07-11 10:49
 */
@Getter
@RequiredArgsConstructor
public enum PayType {

    /**
     * aliPay 支付宝， wxPay微信..等等
     */
    BALANCE("balance", "余额"),
    /**
     * 微信支付
     */
    WECHAT("wxV3Pay", "微信"),
    /**
     * 支付宝
     */
    ALIPAY("aliPay", "支付宝");

    /**
     * 支付类型(支付渠道) 详情查看com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform对应子类，
     */
    @EnumValue
    private final String platform;

    /**
     * 类型描述
     */
    private final String desc;
}
