package com.medusa.gruul.addon.bargain.mq;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author wudi
 */

@RequiredArgsConstructor
@Getter
public enum BargainRabbit implements RabbitParent {


    /**
     * 发起砍价
     */
    BARGAIN_START("bargain.start");

    /**
     * 砍价交换机
     */
    private final String routingKey;

    public static final String EXCHANGE = "bargain.direct";

    @Override
    public String exchange() {
        return BargainRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
