package com.medusa.gruul.global.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: 服务保障枚举
 * @Author: xiaoq
 * @Date : 2022-05-06 16:07
 */
@Getter
@RequiredArgsConstructor
public enum ServiceBarrier {

    /**
     * 全场包邮
     */
    NO_FREIGHT,
    /**
     * 7天退换
     */
    SEVEN_END_BACK,

    /**
     * 48小时发货
     */
    TWO_DAY_SEND,

    /**
     * 假一赔十
     */
    FAKE_COMPENSATE,

    /**
     * 正品保证
     */
    ALL_ENSURE;

}
