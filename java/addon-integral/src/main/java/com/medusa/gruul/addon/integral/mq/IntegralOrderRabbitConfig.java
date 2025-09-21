package com.medusa.gruul.addon.integral.mq;

import com.medusa.gruul.addon.integral.model.enums.IntegralOrderRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 22:42
 **/

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IntegralOrderRabbitConfig {


    /**
     * 积分订单交换机
     */
    @Bean
    public Exchange integralOrderExchange() {
        return ExchangeBuilder.directExchange(IntegralOrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed().build();
    }

    /**
     * -------------创建积分订单-------------------
     */
    @Bean
    public Queue integralOrderCreateFlushData2dbQueue() {
        return QueueBuilder.durable(IntegralOrderQueueNames.INTEGRAL_ORDER_CREATE_FLUSH_DATA2DB_QUEUE)
                .build();
    }

    @Bean
    public Binding interalOrderCreateFlushData2dbBinding() {
        return BindingBuilder.bind(integralOrderCreateFlushData2dbQueue())
                .to(integralOrderExchange())
                .with(IntegralOrderRabbit.INTEGRAL_ORDER_CREATE.routingKey())
                .noargs();
    }

    /**
     * -------------支付超时，关闭积分订单-------------------
     */
    @Bean
    public Queue integralOrderAutoPaidTimeOutCloseQueue() {
        return QueueBuilder.durable(IntegralOrderQueueNames.INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE)
                .build();
    }

    @Bean
    public Binding integralOrderAutoPaidTimeOutCloseBinding() {
        return BindingBuilder.bind(integralOrderAutoPaidTimeOutCloseQueue())
                .to(integralOrderExchange())
                .with(IntegralOrderRabbit.INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey())
                .noargs();
    }

    /**
     * -------------积分订单支付回调-------------------
     */
    @Bean
    public Queue integralPaidCallbackQueue() {
        return QueueBuilder.durable(IntegralOrderQueueNames.INTEGRAL_ORDER_PAID_CALLBACK_QUEUE)
                .build();
    }

    @Bean
    public Binding integralPaidCallbackBinding() {
        return BindingBuilder.bind(integralPaidCallbackQueue())
                .to(integralOrderExchange())
                .with(IntegralOrderRabbit.INTEGRAL_ORDER_PAID_CALLBACK.routingKey())
                .noargs();
    }

    /**
     * ------------------积分订单发货自动完成------------
     */
    @Bean
    public Queue integralOrderSendCompletionQueue() {
        return QueueBuilder.durable(IntegralOrderQueueNames.INTEGRAL_ORDER_SEND_COMPLETION)
                .build();
    }

    @Bean
    public Binding integralOrderSendCompletionBinding() {
        return BindingBuilder.bind(integralOrderSendCompletionQueue())
                .to(integralOrderExchange())
                .with(IntegralOrderRabbit.INTEGRAL_ORDER_SEND_COMPLETION)
                .noargs();
    }

    /**
     * -----------------正常订单完成-------------
     */
    @Bean
    public Queue userOrderFinishBehaviorQueue() {
        return QueueBuilder.durable(IntegralOrderQueueNames.INTEGRAL_USER_ORDER_FINISH_BEHAVIOR)
                .build();
    }

    @Bean
    public Binding userOrderFinishBehaviorBinding() {
        return BindingBuilder.bind(userOrderFinishBehaviorQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_ACCOMPLISH.routingKey())
                .noargs();
    }

}
