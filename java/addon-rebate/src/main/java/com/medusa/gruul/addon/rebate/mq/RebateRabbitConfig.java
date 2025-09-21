package com.medusa.gruul.addon.rebate.mq;

import com.medusa.gruul.common.model.enums.StatementRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.enums.OwnerType;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RebateRabbitConfig {


    /**
     * 订单交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .delayed()
                .build();
    }

    /**
     * 订单创建
     */
    @Bean
    public Queue rebateCreatedQueue() {
        return QueueBuilder.durable(RebateRabbitQueueNames.REBATE_ORDER_CREATED)
                .build();
    }

    @Bean
    public Binding rebateCreateQueueBinding() {
        return BindingBuilder
                .bind(rebateCreatedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATED.routingKey())
                .noargs();
    }

    /**
     * 订单支付
     */
    @Bean
    public Queue rebateOrderPayedQueue() {
        return QueueBuilder.durable(RebateRabbitQueueNames.REBATE_ORDER_PAYED)
                .build();
    }

    @Bean
    public Binding rebateOrderPayedQueueBinding() {
        return BindingBuilder.bind(rebateOrderPayedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 订单已关闭
     */
    @Bean
    public Queue rebateOrderClosedQueue() {
        return QueueBuilder.durable(RebateRabbitQueueNames.REBATE_ORDER_CLOSED)
                .build();
    }

    @Bean
    public Binding rebateOrderClosedQueueBinding() {
        return BindingBuilder.bind(rebateOrderClosedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 订单已完成
     */

    @Bean
    @ConditionalOnMissingBean(name = "overviewExchange")
    public Exchange overviewExchange() {
        return ExchangeBuilder.directExchange(StatementRabbit.EXCHANGE)
                .durable(true)
                .delayed().build();
    }

    @Bean
    public Queue rebateOrderAccomplishQueue() {
        return QueueBuilder.durable(RebateRabbitQueueNames.REBATE_ORDER_ACCOMPLISH)
                .build();
    }

    @Bean
    public Binding rebateOrderAccomplishQueueBinding() {
        return BindingBuilder.bind(rebateOrderAccomplishQueue())
                .to(overviewExchange())
                .with(StatementRabbit.PLATFORM_SERVICE_CHARGE.routingKey())
                .noargs();
    }

    /**
     * 提现工单审核未通过
     */
    @Bean
    public Queue rebateWithdrawOrderForbiddenQueue() {
        return QueueBuilder.durable(RebateRabbitQueueNames.WITHDRAW_ORDER_FORBIDDEN)
                .build();
    }

    @Bean
    public Binding rebateWithdrawOrderForbiddenQueueBinding() {
        return BindingBuilder.bind(rebateWithdrawOrderForbiddenQueue())
                .to(overviewExchange())
                .with(OverviewRabbit.WITHDRAW_ORDER_FORBIDDEN.routingKey() + OwnerType.REBATE.name())
                .noargs();
    }


}
