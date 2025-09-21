package com.medusa.gruul.carrier.pigeon.api.sms.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * Description 消息队列枚举配置
 *
 * @author xiaoq
 * @since 2022-04-13 10:42
 */
@RequiredArgsConstructor
public enum SmsRabbit implements RabbitParent {
    
    /**
     * 短信发送
     */
    SMS_SEND("sms.send.new"),
    ;

    private static final String EXCHANGE_NAME = "gruul.sms.direct";

    /**
     * 路由键
     */
    private final String routeKey;

    @Override
    public String exchange() {
        return EXCHANGE_NAME;
    }

    @Override
    public String routingKey() {
        return routeKey;
    }
}
