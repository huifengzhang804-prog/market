package com.medusa.gruul.order.service.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由模式
 *
 * @author 张治保
 * date 2022/5/25
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderRabbitConfig {


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
     * 订单信息输入数据库队列
     */
    @Bean
    public Queue orderCreateFlushData2dbQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_CREATE_FLUSH_DATA2DB_QUEUE)
                .build();
    }

    @Bean
    public Binding orderCreateFlushData2dbBinding() {
        return BindingBuilder.bind(orderCreateFlushData2dbQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATE.routingKey())
                .noargs();
    }


    /**
     * 订单超时未支付 关闭订单 ---------------------------------------------------------------------------------------------
     */
    @Bean
    public Queue orderAutoPaidTimeOutCloseQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE)
                .build();
    }

    @Bean
    public Binding orderAutoPaidTimeOutCloseBinding() {
        return BindingBuilder.bind(orderAutoPaidTimeOutCloseQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey())
                .noargs();
    }


    /**
     * 支付回调
     */
    @Bean
    public Queue paidCallbackQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_PAID_CALLBACK_QUEUE)
                .build();
    }

    @Bean
    public Binding paidCallbackBinding() {
        return BindingBuilder.bind(paidCallbackQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_CALLBACK.routingKey())
                .noargs();
    }

    /**
     * 超时自动确认收货
     */
    @Bean
    public Queue orderAutoConfirmReceiptQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_AUTO_CONFIRM_RECEIPT_QUEUE)
                .build();
    }

    @Bean
    public Binding orderAutoConfirmReceiptBinding() {
        return BindingBuilder.bind(orderAutoConfirmReceiptQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_AUTO_CONFIRM_RECEIPT.routingKey())
                .noargs();
    }

    /**
     * 超时自动确认收货
     */
    @Bean
    public Queue orderAutoCommentQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_AUTO_COMMENT_QUEUE)
                .build();
    }

    @Bean
    public Binding orderAutoCommentBinding() {
        return BindingBuilder.bind(orderAutoCommentQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_AUTO_COMMENT.routingKey())
                .noargs();
    }

    /**
     * 小程序订单，发货申请queue
     */
    @Bean
    public Queue miniAppOrderDeliveryQueue() {
        return QueueBuilder.durable(OrderQueueNames.MINI_APP_ORDER_DELIVERY_QUEUE)
                .build();
    }

    @Bean
    public Binding miniAppOrderDeliveryBinding() {
        return BindingBuilder.bind(miniAppOrderDeliveryQueue())
                .to(orderExchange())
                .with(OrderRabbit.MINI_APP_ORDER_DELIVER_GOODS.routingKey())
                .noargs();
    }

    /**
     * 小程序订单，重新发货录入 queue
     */
    @Bean
    public Queue miniAppOrderReturnGoodsDeliveryQueue() {
        return QueueBuilder.durable(OrderQueueNames.MINI_APP_ORDER_RETURN_GOODS_DELIVERY_QUEUE)
                .build();
    }

    @Bean
    public Binding miniAppOrderReturnGoodsDeliveryBinding() {
        return BindingBuilder.bind(miniAppOrderReturnGoodsDeliveryQueue())
                .to(orderExchange())
                .with(OrderRabbit.MINI_APP_ORDER_RETURN_GOODS.routingKey())
                .noargs();
    }


    /**
     * 监听订单支付成功
     */
    @Bean
    public Queue orderOrderPaidQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_PAID_QUEUE)
                .build();
    }

    @Bean
    public Binding orderOrderPaidQueueBinding() {
        return BindingBuilder.bind(orderOrderPaidQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 打印小票
     */
    @Bean
    public Queue orderPrintQueue() {
        return QueueBuilder.durable(OrderQueueNames.ORDER_PRINT_QUEUE)
                .build();
    }

    @Bean
    public Binding orderPrintBinding() {
        return BindingBuilder.bind(orderPrintQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PRINT.routingKey())
                .noargs();
    }
}
