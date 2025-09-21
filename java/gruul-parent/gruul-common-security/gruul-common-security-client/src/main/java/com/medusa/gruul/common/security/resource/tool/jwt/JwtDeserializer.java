package com.medusa.gruul.common.security.resource.tool.jwt;

import io.jsonwebtoken.io.AbstractDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;

/**
 * @author 张治保
 * @since 2023/11/28
 */
@Slf4j
public final class JwtDeserializer<T> extends AbstractDeserializer<T> {

    @Override
    protected T doDeserialize(Reader reader) throws IOException {
//        return (T) JSONB.parseObject(new byte[0], Map.class);
        throw new UnsupportedOperationException("Not implemented");

    }
}
