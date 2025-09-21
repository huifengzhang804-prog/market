package com.medusa.gruul.common.mq.rabbit;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.medusa.gruul.common.fastjson2.filter.AutoTypeSupportFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author 张治保
 * date 2023/1/10
 */
@Slf4j
@EnableConfigurationProperties(CustomRabbitProperties.class)
@RequiredArgsConstructor
public class RabbitConfig {

    private final CustomErrorHandler customErrorHandler;
    private final CustomRabbitProperties customRabbitProperties;

    /**
     * fastJson2MessageConverter
     * 使用fastjson2做为消息转换器
     * 生成消息id
     *
     * @return fastJson2MessageConverter
     */
    @Bean
    public SmartMessageConverter fastJson2MessageConverter() {
        FastJson2MessageConvert messageConvert = new FastJson2MessageConvert();
        FastJsonConfig config = messageConvert.getConfig();
        config.setJSONB(true);
        config.setWriterFeatures(
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.ReferenceDetection,
                JSONWriter.Feature.NotWriteDefaultValue,
                JSONWriter.Feature.NotWriteHashMapArrayListClassName,
                JSONWriter.Feature.WriteNameAsSymbol
        );
        config.setReaderFeatures(
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.UseNativeObject,
                JSONReader.Feature.IgnoreAutoTypeNotMatch,
                JSONReader.Feature.FieldBased
        );
        //auto Type 支持
        config.setReaderFilters(AutoTypeSupportFilter.DEFAULT_INSTANCE);
        messageConvert.setCreateMessageIds(Boolean.TRUE);
        return messageConvert;
    }


    /**
     * 批量消费mq 工厂
     * {@link RabbitConstant#BATCH_LISTENER_CONTAINER_FACTORY}
     */
    @Bean(RabbitConstant.BATCH_LISTENER_CONTAINER_FACTORY)
    public SimpleRabbitListenerContainerFactory batchListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setBatchListener(Boolean.TRUE);
        //触发消费这消费 有两个条件 1. 超时 2. 达到批量消费的数量
        //每次批量消费的数量 
        containerFactory.setBatchSize(customRabbitProperties.getBatchSize());
        //消费这等待消费的时间 
        containerFactory.setReceiveTimeout(customRabbitProperties.getReceiveTimeout().toMillis());
        containerFactory.setConsumerBatchEnabled(Boolean.TRUE);
        containerFactory.setAcknowledgeMode(customRabbitProperties.getBatchAckMode());
        containerFactory.setErrorHandler(customErrorHandler);
        return containerFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送到exchange成功,id: {}", correlationData);
                return;
            }
            log.error("消息发送到exchange失败,原因: {}", cause);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplate.ReturnsCallback returnsCallback() {
        return returned ->
                log.error(
                        "消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}",
                        returned.getMessage(),
                        returned.getReplyCode(),
                        returned.getReplyText(),
                        returned.getExchange(),
                        returned.getRoutingKey()
                );
    }


    @Bean
    public InitializingBean rabbitInitializingBean(
            List<AbstractRabbitListenerContainerFactory<?>> factories,
            RabbitTemplate rabbitTemplate,
            @Nullable RabbitTemplate.ConfirmCallback confirmCallback,
            @Nullable RabbitTemplate.ReturnsCallback returnsCallback
    ) {
        factories.forEach(factory -> factory.setErrorHandler(customErrorHandler));
        return () -> {
            // 消息确认 回调
            if (confirmCallback != null) {
                rabbitTemplate.setConfirmCallback(confirmCallback);
            }
            if (returnsCallback != null) {
                rabbitTemplate.setReturnsCallback(returnsCallback);
            }
        };
    }


}
