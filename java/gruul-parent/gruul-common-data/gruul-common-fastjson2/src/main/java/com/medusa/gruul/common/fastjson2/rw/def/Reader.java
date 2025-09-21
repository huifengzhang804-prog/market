package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import java.lang.reflect.Type;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Reader<T> extends ObjectReader<T> {

    /**
     * 反序列化数据
     *
     * @param jsonReader jsonReader
     * @param fieldType  字段类型
     * @param fieldName  字段名称
     * @param features   特性
     * @return 反序列化后的数据
     */
    @Override
    default T readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        if (jsonReader.isNull()) {
            return null;
        }
        return read(jsonReader);
    }

    /**
     * 反序列化数据
     *
     * @param jsonReader jsonReader
     * @return 反序列化后的数据
     */
    T read(JSONReader jsonReader);
}
