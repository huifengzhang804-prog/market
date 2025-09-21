package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Writer<T> extends ObjectWriter<T> {

    /**
     * write
     *
     * @param jsonWriter jsonWriter
     * @param object     原始数据
     * @param fieldName  字段名称
     * @param fieldType  字段类型
     * @param features   特性
     */
    @Override
    @SuppressWarnings("unchecked")
    default void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }
        doWrite(jsonWriter, (T) object);
    }

    /**
     * write
     *
     * @param jsonWriter jsonWriter
     * @param data       原始数据
     */

    void doWrite(JSONWriter jsonWriter, T data);
}
