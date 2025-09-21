package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * 支付币种类型
 *
 *
 * @author xiaoq
 * @ Description 支付货币类型
 * @date 2022-07-25 14:05
 */
@Getter
@RequiredArgsConstructor
public enum FeeType {

    /**
     * 货币类型 CNY：人民币(默认使用人民币作为单位)
     */
    CNY("CNY"),
    /**
     * 美元
     */
    USD("USD");

    @EnumValue
    private final String type;
}
