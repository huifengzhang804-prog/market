package com.medusa.gruul.afs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 退货类型
 *
 * @author xiaoq
 * @Description RefundType.java
 * @date 2023-07-06 15:00
 */
@Getter
@RequiredArgsConstructor
public enum RefundType {

    /**
     * 快递退货
     */
    EXPRESS_REFUND(0),

    /**
     * 到店退货
     */
    GO_STORE_REFUND(1);

    @EnumValue
    private final Integer value;
}
