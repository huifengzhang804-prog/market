package com.medusa.gruul.addon.full.reduction.mq;


import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FullReductionRabbitConfig {

    /**
     * 订单 exchange
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
     * 满减活动订单信息
     */
    @Bean
    public Queue fullReductionPaymentInfoQueue() {
        return QueueBuilder.durable(FullReductionRabbitQueueNames.FULL_REDUCTION_ORDER_PAID_QUEUE)
                .build();
    }

    @Bean
    public Binding fullReductionPaymentInfoBinding() {
        return BindingBuilder.bind(fullReductionPaymentInfoQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }
}

