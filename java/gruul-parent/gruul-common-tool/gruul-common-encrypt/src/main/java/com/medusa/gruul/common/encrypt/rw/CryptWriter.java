package com.medusa.gruul.common.encrypt.rw;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.encrypt.CryptHelper;
import com.medusa.gruul.common.fastjson2.FastJson2;

import java.lang.reflect.Type;

/**
 * 参数加密序列化器
 *
 * @author 张治保
 * @since 2023/11/27
 */
public class CryptWriter implements ObjectWriter<Object> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeString(CryptHelper.encrypt(FastJson2.convert(object, String.class)));
    }
}
