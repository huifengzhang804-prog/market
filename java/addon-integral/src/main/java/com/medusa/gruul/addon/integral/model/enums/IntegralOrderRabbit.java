package com.medusa.gruul.addon.integral.model.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * 积分订单 rabbitmq枚举
 *
 * @author shishuqian
 * date 2023/2/2
 * time 13:27
 **/
@RequiredArgsConstructor
public enum IntegralOrderRabbit implements RabbitParent {

    /**
     * 创建订单
     */
    INTEGRAL_ORDER_CREATE("integral.order.create"),

    /**
     * 订单创建失败 通知
     */
    INTEGRAL_ORDER_CREATE_FAILED("integral.order.create.failed"),


    /**
     * 积分订单支付超时 自动关闭
     */
    INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE("integral.order.auto.paidTimeOutCLose"),

    /**
     * 积分订单支付成功 mq的routing key
     */
    INTEGRAL_ORDER_PAID_CALLBACK("integral.order.paid.callback"),
    /**
     * 积分订单发货自动完成
     */
    INTEGRAL_ORDER_SEND_COMPLETION("integral.order.send.completion")
    ;

    public static final String EXCHANGE = "integral.order.direct";
    /**
     * 路由key
     */
    private final String routingKey;

    @Override
    public String exchange() {
        return IntegralOrderRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
