package com.medusa.gruul.afs.service.mq;

import com.medusa.gruul.afs.api.enums.AfsRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/8
 */
@Configuration
public class AfsRabbitConfig {

    /**
     * 售后交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "afsExchange")
    public Exchange afsExchange() {
        return ExchangeBuilder.directExchange(AfsRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
                .delayed()
                .build();
    }

    /**
     * 系统自动同意售后申请队列
     */
    @Bean
    public Queue afsAutoAgreeRequestQueue() {
        return QueueBuilder
                .durable(AfsQueueNames.AFS_AUTO_AGREE_REQUEST_QUEUE)
                .build();
    }

    @Bean
    public Binding afsAutoAgreeRequestBinding() {
        return BindingBuilder.bind(afsAutoAgreeRequestQueue())
                .to(afsExchange())
                .with(AfsRabbit.AFS_AUTO_AGREE_REQUEST.routingKey())
                .noargs();
    }

    /**
     * 系统自动确认商家收到退货队列
     */
    @Bean
    public Queue afsAutoConfirmReturnedRefundQueue() {
        return QueueBuilder
                .durable(AfsQueueNames.AFS_AUTO_CONFIRM_RETURNED_QUEUE)
                .build();
    }

    @Bean
    public Binding afsAutoConfirmReturnedRefundBinding() {
        return BindingBuilder.bind(afsAutoConfirmReturnedRefundQueue())
                .to(afsExchange())
                .with(AfsRabbit.AFS_AUTO_CONFIRM_RETURNED.routingKey())
                .noargs();
    }

    /**
     * 系统自动关闭售后 队列
     */
    @Bean
    public Queue afsAutoCloseQueue() {
        return QueueBuilder
                .durable(AfsQueueNames.AFS_AUTO_CLOSE_QUEUE)
                .build();

    }

    @Bean
    public Binding afsAutoCloseBinding() {
        return BindingBuilder.bind(afsAutoCloseQueue())
                .to(afsExchange())
                .with(AfsRabbit.AFS_AUTO_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 退款回调队列
     */
    @Bean
    public Queue afsRefundedQueue() {
        return QueueBuilder.durable(AfsQueueNames.AFS_REFUNDED_QUEUE)
                .build();
    }

    @Bean
    public Binding afsRefundedBinding() {
        return BindingBuilder.bind(afsRefundedQueue())
                .to(afsExchange())
                .with(AfsRabbit.AFS_REFUND_CALLBACK.routingKey())
                .noargs();
    }


}
