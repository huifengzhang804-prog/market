package com.medusa.gruul.carrier.pigeon.service.mq;

import com.medusa.gruul.carrier.pigeon.api.enums.PigeonRabbit;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/10/19
 */
@Configuration
public class PigeonRabbitConfig {

    @Bean
    @ConditionalOnMissingBean(name = "pigeonExchange")
    public Exchange pigeonExchange() {
        return ExchangeBuilder.directExchange(PigeonRabbit.EXCHANGE)
                .durable(true)
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
     * 短信消息实际消费队列所绑定的交换机
     */
    @Bean
    public Exchange smsExchange() {
        return new DirectExchange(SmsRabbit.SMS_SEND.exchange(), true, false);
    }


    /**
     * 短信发送队列绑定交换机
     */
    @Bean
    public Queue smsSendQueue() {
        return new Queue(PigeonRabbitQueueNames.PIGEON_SEND_SMS, true);
    }

    @Bean
    public Binding smsSendBinding() {
        return BindingBuilder.bind(smsSendQueue())
                .to(smsExchange())
                .with(SmsRabbit.SMS_SEND.routingKey())
                .noargs();
    }

    /**
     * 提醒消息 队列绑定
     */
    @Bean
    public Queue sendMessageQueue() {
        return QueueBuilder.durable(PigeonRabbitQueueNames.PIGEON_ORDER_PAID_BROADCAST_QUEUE)
                .autoDelete()
                .build();
    }

    @Bean
    public Binding sendMessageBinding() {
        return BindingBuilder.bind(sendMessageQueue())
                .to(orderExchange())
                .with(OrderRabbit.ORDER_PAID_BROADCAST.routingKey())
                .noargs();
    }

    /**
     * 店铺信息修改 队列
     */
    @Bean
    public Queue pigeonShopInfoUpdateQueue() {
        return QueueBuilder.durable(PigeonRabbitQueueNames.PIGEON_SHOP_INFO_UPDATE_QUEUE)
                .build();
    }

    @Bean
    public Binding pigeonShopInfoUpdateBinding() {
        return BindingBuilder.bind(pigeonShopInfoUpdateQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_UPDATE.routingKey())
                .noargs();
    }


    /**
     * 小程序订阅消息
     */
    @Bean
    public Queue sendAppletSubscribeQueue() {
        return QueueBuilder.durable(PigeonRabbitQueueNames.PIGEON_APPLET_SUBSCRIBE_QUEUE)
                .build();
    }

    @Bean
    public Binding sendAppletSubscribeBinding() {
        return BindingBuilder.bind(sendAppletSubscribeQueue())
                .to(pigeonExchange())
                .with(PigeonRabbit.PIGEON_APPLET_SUBSCRIBE.routingKey())
                .noargs();
    }

    /**
     * uaa交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "uaaExchange")
    public Exchange uaaExchange() {
        return ExchangeBuilder.directExchange(UaaRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 用户修改个人资料
     */
    @Bean
    public Queue pigeonUpdateUserDataQueue() {
        return QueueBuilder.durable(PigeonRabbitQueueNames.USER_DATA_UPDATE)
                .build();
    }

    @Bean
    public Binding pigeonUpdateUserDataBinding() {
        return BindingBuilder.bind(pigeonUpdateUserDataQueue())
                .to(uaaExchange())
                .with(UaaRabbit.UPDATE_DATA.routingKey())
                .noargs();
    }
}
