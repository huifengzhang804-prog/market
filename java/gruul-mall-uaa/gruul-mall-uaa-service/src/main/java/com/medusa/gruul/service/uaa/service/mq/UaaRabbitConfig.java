package com.medusa.gruul.service.uaa.service.mq;

import com.medusa.gruul.carrier.pigeon.api.enums.PigeonRabbit;
import com.medusa.gruul.service.uaa.api.enums.UaaRabbit;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import java.util.UUID;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * <p>
 * </p>
 *
 * @author 张治保
 * date 2022/5/25
 */
@Configuration
public class UaaRabbitConfig  {


    /**
     * uaa 交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "uaaExchange")
    public Exchange uaaExchange() {
        return ExchangeBuilder.directExchange(UaaRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 店铺交换机
     */
    @Bean
    @ConditionalOnMissingBean(name = "shopExchange")
    public Exchange shopExchange() {
        return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "pigeonExchange")
    public Exchange pigeonChatExchange() {
        return ExchangeBuilder.directExchange(PigeonRabbit.EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 生成临时队列短信模拟状态发生变化的消息
     * @return
     */
    @Bean
    public Queue smsSimulationQueue() {
        // 随机生成队列名称
        String randomQueueName = "smsSimulationQueue-" + UUID.randomUUID();
        return new Queue(randomQueueName, false, false, true); // durable = true，表示队列持久化
    }

    // 创建队列和交换机的绑定
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(smsSimulationQueue())
                .to(pigeonChatExchange())
                .with(PigeonRabbit.SMS_SIMULATION.routingKey()).noargs(); // 使用随机队列名称作为路由键
    }

    // 动态创建并注册消息监听容器
    @Bean
    public MessageListenerContainer messageListenerContainer(ApplicationContext applicationContext) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(applicationContext.getBean(CachingConnectionFactory.class));
        // 设置监听的队列名称
        container.setQueues(smsSimulationQueue());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置消息处理器
        container.setMessageListener(new SmsSimulationListener(applicationContext));
        return container;
    }



    /**
     * 店铺管理员切换
     */
    @Bean
    public Queue shopAdminChangeQueue() {
        return QueueBuilder.durable(UaaConstant.SHOP_ADMIN_CHANGE_QUEUE).build();
    }

    @Bean
    public Binding shopAdminChangeBinding() {
        return BindingBuilder
                .bind(shopAdminChangeQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ADMIN_CHANGE.routingKey())
                .noargs();
    }

    /**
     * 启用/禁用店铺
     */
    @Bean
    public Queue shopEnableDisableQueue() {
        return new Queue(UaaConstant.SHOP_ENABLE_DISABLE_QUEUE, true);
    }

    @Bean
    public Binding shopEnableDisableBinding() {
        return BindingBuilder
                .bind(shopEnableDisableQueue())
                .to(shopExchange())
                .with(ShopRabbit.SHOP_ENABLE_DISABLE.routingKey())
                .noargs();
    }

}
