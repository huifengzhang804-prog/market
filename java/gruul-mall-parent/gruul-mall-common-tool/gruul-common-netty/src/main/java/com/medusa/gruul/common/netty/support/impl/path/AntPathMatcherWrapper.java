package com.medusa.gruul.common.netty.support.impl.path;

import com.medusa.gruul.common.netty.domain.WebSocketEndpointServer;
import com.medusa.gruul.common.netty.support.WsPathMatcher;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.EqualsAndHashCode;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author miskw
 * Ant path matcher
 */
@EqualsAndHashCode(of = "pattern", callSuper = false)
public class AntPathMatcherWrapper extends AntPathMatcher implements WsPathMatcher {

    private final String pattern;

    public AntPathMatcherWrapper(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), true, variables);
        if (result) {
            channel.attr(WebSocketEndpointServer.URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
