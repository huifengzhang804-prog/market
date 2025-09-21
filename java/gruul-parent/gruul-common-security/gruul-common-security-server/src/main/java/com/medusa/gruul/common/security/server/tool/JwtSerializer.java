package com.medusa.gruul.common.security.server.tool;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import io.jsonwebtoken.io.AbstractSerializer;

import java.io.OutputStream;

/**
 * jwt 序列化器
 *
 * @author 张治保
 * @since 2023/11/28
 */
public class JwtSerializer<T> extends AbstractSerializer<T> {
    @Override
    protected void doSerialize(T data, OutputStream out) throws Exception {
        try (out) {
            JSONB.writeTo(out, data, JSONWriter.Feature.BeanToArray, JSONWriter.Feature.WriteByteArrayAsBase64);
        }
    }
}
