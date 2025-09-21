package com.medusa.gruul.payment.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaoq
 * @ Description 支付mq信息
 * @date 2022-08-01 15:07
 */
@RequiredArgsConstructor
public enum PaymentRabbit implements RabbitParent {
    ,
    ;

    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE = "payment.direct";

    @Override
    public String exchange() {
        return PaymentRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
