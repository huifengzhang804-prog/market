package com.medusa.gruul.live.service.mq;

import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miskw
 * date 2022/11/18
 * 直播mq配置
 */
@Configuration
public class LiveRabbitConfig {


    /**
     * 创建交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsExchange")
    public Exchange goodsExchange() {
        return new DirectExchange(GoodsRabbit.EXCHANGE, true, false);
    }

    /**
     * 直播商品下架队列
     */
    @Bean
    Queue liveGoodsChangeQueue() {
        return new Queue(LiveRabbitQueueNames.LIVE_CHANGE_QUEUE, true);
    }

    /**
     * 直播绑定
     */
    @Bean
    public Binding liveExchangeBinding() {
        return BindingBuilder
                .bind(liveGoodsChangeQueue())
                .to(goodsExchange())
                .with(GoodsRabbit.GOODS_UPDATE_STATUS.routingKey())
                .noargs();
    }


}
