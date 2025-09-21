package com.medusa.gruul.addon.team.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2023/3/3
 */
@Configuration
@RequiredArgsConstructor
public class TeamRabbitConfig {

    /**
     * 创建订单 ---------------------------------------------------------------------------------------------------------
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
     * 订单已支付消息队列
     */
    @Bean
    public Queue teamOrderPaidQueue() {
        return QueueBuilder
                .durable(TeamRabbitNames.ORDER_PAID)
                .build();
    }

    @Bean
    public Binding teamOrderPaidBinding() {
        return BindingBuilder
                .bind(teamOrderPaidQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

}
