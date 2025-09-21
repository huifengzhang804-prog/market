package com.medusa.gruul.addon.platform.mq;

import com.medusa.gruul.shop.api.enums.ShopRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PlatformRabbitConfig
 *
 * @author xiaoq
 * @Description PlatformRabbitConfig.java
 * @date 2023-09-06 15:31
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PlatformRabbitConfig {
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


    /**
     * 店铺状态改变 启用禁用队列
     *
     * @return Queue
     */
    @Bean
    public Queue platformShopChangeQueue() {
        return new Queue(PlatformQueueNames.SHOP_CHANGE_QUEUE, true);
    }

    @Bean
    public Binding platformShopExchangeBinding() {
        return BindingBuilder
                .bind(platformShopChangeQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ENABLE_DISABLE.routingKey())
                .noargs();
    }




}
