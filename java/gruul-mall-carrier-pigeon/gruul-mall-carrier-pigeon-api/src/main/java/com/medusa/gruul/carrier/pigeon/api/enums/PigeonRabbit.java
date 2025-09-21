package com.medusa.gruul.carrier.pigeon.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * 信鸽mq配置
 *
 * @author 张治保
 * date 2022/6/9
 */
@RequiredArgsConstructor
public enum PigeonRabbit implements RabbitParent {

    /**
     * 小程序订阅消息
     */
    PIGEON_APPLET_SUBSCRIBE("pigeon.applet.subscribe"),
    /**
     * 短信模拟、真实 状态改变路由key
     */
    SMS_SIMULATION("pigeon.sms.simulation")
    ;

    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE = "pigeon.direct";

    @Override
    public String exchange() {
        return PigeonRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
