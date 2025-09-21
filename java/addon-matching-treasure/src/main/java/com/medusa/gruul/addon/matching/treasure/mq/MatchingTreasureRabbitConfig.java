package com.medusa.gruul.addon.matching.treasure.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchingTreasureRabbitConfig {


    /**
     * 商品交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return ExchangeBuilder.directExchange(GoodsRabbit.EXCHANGE)
                .durable(Boolean.TRUE)
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
     * 套餐活动支付信息
     */
    @Bean
    public Queue setMealPaymentInfoQueue() {
        return QueueBuilder.durable(MatchingTreasureQueueNames.MATCHING_TREASURE_PAYMENT_INFO)
                .build();
    }

    @Bean
    public Binding setMealPaymentInfoBinding() {
        return BindingBuilder.bind(setMealPaymentInfoQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 套餐活动支付退款信息
     */
    @Bean
    public Queue setMealRefundInfoQueue() {
        return QueueBuilder.durable(MatchingTreasureQueueNames.MATCHING_TREASURE_REFUND_INFO)
                .build();
    }

    @Bean
    public Binding setMealRefundInfoBinding() {
        return BindingBuilder.bind(setMealRefundInfoQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CLOSE.routingKey())
                .noargs();
    }

    /**
     * 商品更新
     */
    @Bean
    public Queue currProductUpdateQueue() {
        return QueueBuilder.durable(MatchingTreasureQueueNames.PRODUCT_UPDATE)
                .build();
    }


    @Bean
    public Binding currProductUpdateBinding() {
        return BindingBuilder.bind(currProductUpdateQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE.routingKey())
                .noargs();
    }

}
