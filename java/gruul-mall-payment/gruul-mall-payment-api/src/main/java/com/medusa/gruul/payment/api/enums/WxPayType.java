package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * 微信交易类型枚举
 *
 *
 * @author xiaoq
 * @ Description WxPayType.java
 * @date 2022-07-26 13:47
 */
@Getter
@RequiredArgsConstructor
public enum WxPayType {


    /**
     * JSAPI支付（小程序appId支付）
     */
    JSAPI_MINI(1),

    /**
     * Native支付
     */
    Native(2),

    /**
     * app支付
     */
    APP(3),

    /**
     * JSAPI支付（公众号appId支付）
     */
    JSAPI_MP(4),

    /**
     * 商户对个人付款
     */
    ENT_PAY(5),

    /**
     * H5支付
     */
    H5(6),
    ;

    @EnumValue
    private final Integer payType;
}
