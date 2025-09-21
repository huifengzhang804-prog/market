package com.medusa.gruul.cart.service.mq;

import com.medusa.gruul.cart.api.enums.CartRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/7/26
 */
@Configuration
public class CartRabbitConfig {

    @Bean
    @ConditionalOnMissingBean(name = "cartExchange")
    public Exchange cartExchange() {
        return ExchangeBuilder.directExchange(CartRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
                .durable(true)
                .delayed()
                .build();
    }

    @Bean
    public Queue deleteProductQueue() {
        return new Queue(CartQueueNames.CART_DELETE_PRODUCT_QUEUE, true);
    }

    @Bean
    public Binding deleteProductBinding() {
        return BindingBuilder.bind(deleteProductQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_CREATED.routingKey())
                .noargs();
    }

    /**
     * 店铺加购数 修改 队列
     */
    @Bean
    public Queue updateShopCartQueue() {
        return new Queue(CartQueueNames.SHOP_CART_UPDATE, true);
    }

    @Bean
    public Binding updateShopCartBinding() {
        return BindingBuilder.bind(updateShopCartQueue())
                .to(cartExchange())
                .with(CartRabbit.UPDATE_SHOP_CART.routingKey())
                .noargs();
    }


}
