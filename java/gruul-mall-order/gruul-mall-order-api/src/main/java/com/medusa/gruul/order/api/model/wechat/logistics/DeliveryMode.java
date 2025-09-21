package com.medusa.gruul.order.api.model.wechat.logistics;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 微信小程序订单上传 发货模式
 *
 * @author wufuzhong
 * time 2024/01/08 15:11
 * desc 小程序订单上传 发货模式
 */
@Getter
@RequiredArgsConstructor
public enum DeliveryMode {
    /**
     * 统一发货
     */
    UNIFIED_DELIVERY(1),

    /**
     * 分拆发货
     */
    SPLIT_DELIVERY(2);

    @EnumValue
    private final Integer value;
}
