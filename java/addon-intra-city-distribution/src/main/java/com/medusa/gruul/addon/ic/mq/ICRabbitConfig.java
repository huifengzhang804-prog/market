package com.medusa.gruul.addon.ic.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * @since 2024/8/26
 */
@Configuration
public class ICRabbitConfig {

    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    /**
     * 订单已支付
     */
    @Bean
    public Queue icOrderPaidQueue() {
        return QueueBuilder.durable(ICRabbitQueueNames.ORDER_PAID_QUEUE)
                .build();
    }

    @Bean
    public Binding icOrderPaidBinding() {
        return BindingBuilder.bind(icOrderPaidQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }
}
