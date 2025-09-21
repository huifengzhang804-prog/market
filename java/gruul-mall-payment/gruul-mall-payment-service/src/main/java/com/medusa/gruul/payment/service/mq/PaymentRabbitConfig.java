package com.medusa.gruul.payment.service.mq;

import com.medusa.gruul.user.api.enums.UserRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 支付rabbit配置
 * model : 路由模式
 *
 * @author xiaoq
 * Description PaymentRabbitConfig.java
 * Date 2022-09-29 18:03
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentRabbitConfig {


    /**
     * 用户余额变化 -----------------------------------------------------------------------------------
     */
    @Bean
    @ConditionalOnMissingBean(name = "userExchange")
    public Exchange userExchange() {
        return ExchangeBuilder
                .directExchange(UserRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    /**
     * 队列绑定准备
     */
    @Bean
    public Queue usePaymentChangeDetailQueue() {
        return QueueBuilder
                .durable(PaymentQueueNames.PAYMENT_CHANGE_CREATE_DETAIL_QUEUE)
                .build();
    }

    @Bean
    public Binding usePaymentChangeDetailBinding() {
        return BindingBuilder
                .bind(usePaymentChangeDetailQueue())
                .to(userExchange())
                .with(UserRabbit.USER_BALANCE_CHANGE.routingKey())
                .noargs();
    }

}
