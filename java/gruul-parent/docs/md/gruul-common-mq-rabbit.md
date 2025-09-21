# com.medusa.gruul.common.mq.rabbit.CustomErrorHandler

**文件路径**: `mq\rabbit\CustomErrorHandler.java`

## 文件结构
```
项目根目录
└── mq\rabbit
    └── CustomErrorHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.mq.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.lang.NonNull;

@Slf4j
public class CustomErrorHandler extends ConditionalRejectingErrorHandler {
    @Override
    protected void log(@NonNull Throwable err) {
        if (log.isErrorEnabled()) {
            log.error("》》》Rabbit MQ ERROR", err);
        }
    }
}
```

# com.medusa.gruul.common.mq.rabbit.CustomRabbitProperties

**文件路径**: `mq\rabbit\CustomRabbitProperties.java`

## 代码文档
///
@author 张治保
date 2023/1/10
 
///

///
每次批量消费的数量 默认每批300个
     
///

///
消费者等待数据的时间 默认 700毫秒
     
///

///
批量消费ack确认模式 默认手动确认
     
///


## 文件结构
```
项目根目录
└── mq\rabbit
    └── CustomRabbitProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.mq.rabbit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author 张治保
 * date 2023/1/10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.rabbit")
public class CustomRabbitProperties {

    /**
     * 每次批量消费的数量 默认每批300个
     */
    private Integer batchSize = 300;

    /**
     * 消费者等待数据的时间 默认 700毫秒
     */
    private Duration receiveTimeout = Duration.ofMillis(700);

    /**
     * 批量消费ack确认模式 默认手动确认
     */
    private AcknowledgeMode batchAckMode = AcknowledgeMode.MANUAL;


}

```

# com.medusa.gruul.common.mq.rabbit.FastJson2MessageConvert

**文件路径**: `mq\rabbit\FastJson2MessageConvert.java`

## 代码文档
///
@author 张治保
date 2023/5/17
 
///


## 文件结构
```
项目根目录
└── mq\rabbit
    └── FastJson2MessageConvert.java
```

## 完整代码
```java
package com.medusa.gruul.common.mq.rabbit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import lombok.Getter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Type;

/**
 * @author 张治保
 * date 2023/5/17
 */
@Getter
public class FastJson2MessageConvert extends AbstractMessageConverter implements SmartMessageConverter {

	private final FastJsonConfig config = new FastJsonConfig();

	@Override
	@NonNull
	protected Message createMessage(@NonNull Object payload, @NonNull MessageProperties messageProperties) {
		byte[] bytes;
		if (config.isJSONB()) {
			bytes = JSONB.toBytes(payload, config.getSymbolTable(), config.getWriterFilters(), config.getWriterFeatures());
		} else {
			bytes = JSON.toJSONBytes(payload, config.getDateFormat(), config.getWriterFilters(), config.getWriterFeatures());
		}
		return new Message(bytes, messageProperties);
	}

	@Override
	@NonNull
	public Object fromMessage(@NonNull Message message) throws MessageConversionException {
		return this.fromMessage(message, null);
	}

	@Override
	@NonNull
	public Object fromMessage(@NonNull Message message, @Nullable Object hint) throws MessageConversionException {
		byte[] payload = message.getBody();
		Type resolvedType = getResolvedType(hint);
		if (config.isJSONB()) {
			return JSONB.parseObject(payload, resolvedType, config.getSymbolTable(), config.getReaderFilters(), config.getReaderFeatures());
		}
		return JSON.parseObject(payload, resolvedType, config.getDateFormat(), config.getReaderFilters(), config.getReaderFeatures());

	}

	private Type getResolvedType(@Nullable Object hint) {
		if (hint instanceof Type type) {
			return type;
		}
		if (hint instanceof TypeReference<?> reference) {
			return reference.getType();
		}
		if (hint instanceof ParameterizedTypeReference<?> reference) {
			return reference.getType();
		}
		if (hint instanceof MethodParameter param) {
			param = param.nestedIfOptional();
			if (Message.class.isAssignableFrom(param.getParameterType())) {
				param = param.nested();
			}

			Type genericParameterType = param.getNestedGenericParameterType();
			Class<?> contextClass = param.getContainingClass();
			return GenericTypeResolver.resolveType(genericParameterType, contextClass);
		}
		return Object.class;
	}
}

```

# com.medusa.gruul.common.mq.rabbit.RabbitConfig

**文件路径**: `mq\rabbit\RabbitConfig.java`

## 代码文档
///
@author 张治保
date 2023/1/10
 
///

///
fastJson2MessageConverter
使用fastjson2做为消息转换器
生成消息id

@return fastJson2MessageConverter
     
///

///
批量消费mq 工厂
{@link RabbitConstant#BATCH_LISTENER_CONTAINER_FACTORY}
     
///


## 文件结构
```
项目根目录
└── mq\rabbit
    └── RabbitConfig.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.mq.rabbit.RabbitConstant

**文件路径**: `mq\rabbit\RabbitConstant.java`

## 代码文档
///
@author 张治保
date 2023/1/10
 
///

///
批量 消费 ContainerFactory bean名称
     
///


## 文件结构
```
项目根目录
└── mq\rabbit
    └── RabbitConstant.java
```

## 完整代码
```java
package com.medusa.gruul.common.mq.rabbit;

/**
 * @author 张治保
 * date 2023/1/10
 */
public interface RabbitConstant {

    /**
     * 批量 消费 ContainerFactory bean名称
     */
    String BATCH_LISTENER_CONTAINER_FACTORY = "batchListenerContainerFactory";
    

}

```

# com.medusa.gruul.common.mq.rabbit.RabbitI18NLoader

**文件路径**: `mq\rabbit\RabbitI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── mq\rabbit
    └── RabbitI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.mq.rabbit;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class RabbitI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/rabbit");
	}
}

```

