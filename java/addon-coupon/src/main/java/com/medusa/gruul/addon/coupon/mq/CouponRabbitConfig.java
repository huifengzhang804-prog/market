package com.medusa.gruul.addon.coupon.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/11/11
 */
@Configuration
public class CouponRabbitConfig {


    /**
     * 订单 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }


    /**
     * ---------------- 订单创建 归还优惠券
     */
    @Bean
    public Queue couponOrderCreateFailedQueue() {
        return QueueBuilder.durable(CouponRabbitQueueName.COUPON_ORDER_CLOSED)
                .build();
    }

    @Bean
    public Binding couponOrderCreateFailBinding() {
        return BindingBuilder.bind(couponOrderCreateFailedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATE_FAILED.routingKey())
                .noargs();
    }

    /**
     * ---------------- 订单关闭 归还优惠券
     */
    @Bean
    public Binding couponOrderCloseBinding() {
        return BindingBuilder.bind(couponOrderCreateFailedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }
}
