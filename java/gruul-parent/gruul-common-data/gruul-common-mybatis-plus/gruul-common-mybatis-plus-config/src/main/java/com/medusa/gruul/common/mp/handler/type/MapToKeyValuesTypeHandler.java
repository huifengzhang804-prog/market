package com.medusa.gruul.common.mp.handler.type;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.global.model.o.KeyValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 处理复杂类型的 KEY 的 map 序列化 反序列化 处理器
 *
 * @author 张治保
 * date 2023/7/28
 * <K> key 类型
 * <V> value 类型
 */
@MappedTypes({Map.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class MapToKeyValuesTypeHandler extends Fastjson2TypeHandler {


    public MapToKeyValuesTypeHandler(Class<?> type) {
        super(type);
    }

    public MapToKeyValuesTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }


    @Override
    @SuppressWarnings("rawtypes")
    public Map<Object, Object> parse(String json) {
        if (StrUtil.isEmpty(json)) {
            return new HashMap<>(0);
        }
        Type fieldType = getFieldType();
        if (fieldType instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length != 2) {
                throw new RuntimeException("MapTypeHandler only support Map<K, V>");
            }
            fieldType = new ParameterizedTypeImpl(actualTypeArguments, null, KeyValue.class);
        }
        List<KeyValue> keyValues = JSON.parseArray(json, fieldType);
        Map<Object, Object> result = new HashMap<>(keyValues.size());
        for (KeyValue keyValue : keyValues) {
            result.put(keyValue.getKey(), keyValue.getValue());
        }
        return result;
    }

    @Override
    public String toJson(Object obj) {
        if (!(obj instanceof Map<?, ?> keyValueMap)) {
            throw new RuntimeException("MapTypeHandler only support Map");
        }
        Set<KeyValue<?, ?>> keyValues = new HashSet<>(keyValueMap.size());
        for (Map.Entry<?, ?> entry : keyValueMap.entrySet()) {
            keyValues.add(new KeyValue<>().setKey(entry.getKey()).setValue(entry.getValue()));
        }
        return JSON.toJSONString(keyValues, JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty);
    }

    @Getter
    @Setter
    @ToString
    public static class Bean {

        private Map<Long, Transaction> map;

    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Transaction implements Serializable {
        /**
         * 交易流水号
         */
        private String transactionId;

        /**
         * 是否可分账
         */
        private Boolean profitSharing;
    }
}
