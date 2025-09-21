package com.medusa.gruul.addon.seckill.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author WuDi
 * date: 2022/12/5
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecKillRabbitConfig {

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
     * 订单支付
     */
    @Bean
    public Queue seckillOrderPaid() {
        return QueueBuilder.durable(SeckillQueueNames.SECKILL_ORDER_PAID)
                .build();
    }

    @Bean
    public Binding secKillPaymentInfoBinding() {
        return BindingBuilder.bind(seckillOrderPaid())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

}
