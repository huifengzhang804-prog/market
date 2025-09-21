package com.medusa.gruul.common.netty.support.impl.path;

import com.medusa.gruul.common.netty.support.WsPathMatcher;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.EqualsAndHashCode;

/**
 * @author miskw
 * Default path matcher
 */
@EqualsAndHashCode
public class DefaultPathMatcher implements WsPathMatcher {

    private final String pattern;

    public DefaultPathMatcher(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        return pattern.equals(decoder.path());
    }
}
