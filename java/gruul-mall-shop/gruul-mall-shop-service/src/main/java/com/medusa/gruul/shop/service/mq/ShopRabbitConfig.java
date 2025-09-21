package com.medusa.gruul.shop.service.mq;

import com.medusa.gruul.shop.api.enums.ShopRabbit;
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
@Configuration
public class ShopRabbitConfig {


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

    @Bean
    public Queue shopOnShelfGoodsCountChangeQueue() {
        return QueueBuilder.durable(ShopQueueNames.SHOP_GOODS_COUNT_CHANGE_QUEUE)
                .build();
    }

    /**
     * 店铺上架商品数量变更队列绑定交换机
     *
     * @return
     */
    @Bean
    public Binding shopOnShelfGoodsCountChangeQueueBinding() {
        return BindingBuilder.bind(shopOnShelfGoodsCountChangeQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.routingKey())
                .noargs();
    }


}
