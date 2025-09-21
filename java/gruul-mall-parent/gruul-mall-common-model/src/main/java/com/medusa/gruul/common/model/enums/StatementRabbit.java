package com.medusa.gruul.common.model.enums;

import lombok.RequiredArgsConstructor;

/**
 * 对账单mq
 *
 * @author: WuDi
 * @date: 2022/10/14
 */
@RequiredArgsConstructor
public enum StatementRabbit implements RabbitParent {

    /**
     * 对账单
     */
    OVERVIEW_STATEMENT("overview.statement"),

    /**
     * 平台服务费
     */
    PLATFORM_SERVICE_CHARGE("overview.serviceCharge"),;

    private final String routingKey;

    public static final String EXCHANGE = "overview.direct";

    @Override
    public String exchange() {
        return StatementRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
    }
