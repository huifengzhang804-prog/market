package com.medusa.gruul.common.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * hash key 序列化方式 非 String 转 string ，
 * 参考{@link StringRedisSerializer}
 *
 * @author 张治保
 * @since 2024/7/2
 */
public class HashKeySerializer implements RedisSerializer<Object> {
    private final Charset charset;

    public HashKeySerializer() {
        this(StandardCharsets.UTF_8);
    }

    public HashKeySerializer(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] serialize(@Nullable Object value) throws SerializationException {
        if (value == null) {
            return null;
        }
        return (value instanceof String str ? str : String.valueOf(value)).getBytes(charset);
    }

    @Override
    public Object deserialize(@Nullable byte[] bytes) throws SerializationException {
        return (bytes == null ? null : new String(bytes, charset));
    }
}
