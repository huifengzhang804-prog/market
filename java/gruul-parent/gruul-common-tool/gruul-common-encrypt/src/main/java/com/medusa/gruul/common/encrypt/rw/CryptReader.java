package com.medusa.gruul.common.encrypt.rw;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.medusa.gruul.common.encrypt.CryptHelper;
import com.medusa.gruul.common.fastjson2.FastJson2;

import java.lang.reflect.Type;

/**
 * 参数解密反序列化器
 *
 * @author 张治保
 * @since 2023/11/27
 */
public class CryptReader implements ObjectReader<Object> {

    @Override
    public Object readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        String str = jsonReader.readString();
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        return FastJson2.convert(CryptHelper.decrypt(str), (Class<?>) fieldType);
    }
}