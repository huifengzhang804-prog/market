package com.medusa.gruul.addon.bargain.mq;


import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wudi
 */
@Configuration
public class BargainRabbitConfig {

    /**
     * 砍价交换机
     */
    @Bean
    public Exchange bargainExchange() {
        return ExchangeBuilder.directExchange(BargainRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .delayed()
                .build();
    }

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
     * 发起砍价队列
     */
    @Bean
    public Queue bargainStartQueue() {
        return QueueBuilder.durable(BargainQueueNames.BARGAIN_START)
                .build();
    }

    @Bean
    public Binding bargainStartBinding() {
        return BindingBuilder.bind(bargainStartQueue())
                .to(bargainExchange())
                .with(BargainRabbit.BARGAIN_START.routingKey())
                .noargs();
    }

    /**
     * 砍价活动支付信息
     */
    @Bean
    public Queue bargainPaymentInfoQueue() {
        return QueueBuilder.durable(BargainQueueNames.BARGAIN_ORDER_PAID)
                .build();
    }

    @Bean
    public Binding bargainPaymentInfoBinding() {
        return BindingBuilder.bind(bargainPaymentInfoQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 砍价活动退款信息
     */
    @Bean
    public Queue bargainRefundInfoQueue() {
        return QueueBuilder.durable(BargainQueueNames.BARGAIN_REFUND_INFO)
                .build();
    }

    @Bean
    public Binding bargainRefundInfoBinding() {
        return BindingBuilder.bind(bargainRefundInfoQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 砍价订单创建失败
     */
    @Bean
    public Queue bargainOrdeCreateFailQueue() {
        return QueueBuilder.durable(BargainQueueNames.BARGAIN_ORDER_CREATE_FAIL)
                .build();
    }
    
    @Bean
    public Binding bargainOrdeCreateFailBinding() {
        return BindingBuilder.bind(bargainOrdeCreateFailQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATE_FAILED.routingKey())
                .noargs();
    }

}
