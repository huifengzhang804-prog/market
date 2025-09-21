package com.medusa.gruul.addon.store.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 店铺门店RabbitConfig
 *
 * @author xiaoq
 * @since 2023-03-15 21:42
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ShopStoreRabbitConfig {

    /**
     * 店铺 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

// -------------------------------------------店铺信息修改---------------------------------------------------
// -------------------------------------------店铺信息修改---------------------------------------------------

    @Bean
    public Queue storeShopInfoUpdateQueue() {
        return QueueBuilder.durable(ShopStoreQueueNames.SHOP_INFO_UPDATE_QUEUE)
                .build();
    }

    @Bean
    public Binding storeShopInfoUpdateBinding() {
        return BindingBuilder
                .bind(storeShopInfoUpdateQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }


    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }


// -------------------------------------------------订单支付成功--------------------------------------------
// -------------------------------------------------订单支付成功--------------------------------------------

    @Bean
    public Queue storeOrderPaySucceedQueue() {
        return QueueBuilder.durable(ShopStoreQueueNames.STORE_ORDER_PAY_SUCCEED_QUEUE)
                .build();
    }

    @Bean
    public Binding storeOrderPaySucceedBinding() {
        return BindingBuilder
                .bind(storeOrderPaySucceedQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }


// -------------------------------------------------订单发货--------------------------------------------
// -------------------------------------------------订单发货--------------------------------------------

    @Bean
    public Queue storeOrderDeliverGoodsQueue() {
        return QueueBuilder
                .durable(ShopStoreQueueNames.STORE_ORDER_DELIVER_GOODS_QUEUE)
                .build();
    }

    @Bean
    public Binding storeOrderDeliverGoodsBinding() {
        return BindingBuilder
                .bind(storeOrderDeliverGoodsQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_DELIVER_GOODS.routingKey())
                .noargs();
    }


// -------------------------------------------------订单完成--------------------------------------------
// -------------------------------------------------订单完成--------------------------------------------

    @Bean
    public Queue storeOrderCompleteQueue() {
        return QueueBuilder.durable(ShopStoreQueueNames.STORE_ORDER_COMPLETE_QUEUE)
                .build();
    }


    @Bean
    public Binding storeOrderCompleteBinding() {
        return BindingBuilder
                .bind(storeOrderCompleteQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_ACCOMPLISH.routingKey())
                .noargs();
    }

}
