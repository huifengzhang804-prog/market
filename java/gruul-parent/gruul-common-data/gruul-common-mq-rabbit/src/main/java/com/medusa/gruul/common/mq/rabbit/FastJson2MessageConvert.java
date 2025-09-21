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
