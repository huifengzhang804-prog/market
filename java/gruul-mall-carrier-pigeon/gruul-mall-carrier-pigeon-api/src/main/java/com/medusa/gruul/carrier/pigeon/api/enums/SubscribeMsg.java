package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author WuDi
 * date: 2023/1/9
 */
@Getter
@RequiredArgsConstructor
public enum SubscribeMsg{

    /**
     * 订单支付
     */
    ORDER_PAY(0),
    /**
     * 直播开播
     */
    LIVE_DEBUT(1);

    @EnumValue
    private final Integer value;





}
