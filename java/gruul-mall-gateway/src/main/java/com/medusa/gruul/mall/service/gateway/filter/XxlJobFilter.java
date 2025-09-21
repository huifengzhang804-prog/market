package com.medusa.gruul.mall.service.gateway.filter;

import com.medusa.gruul.common.model.constant.Xxl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class XxlJobFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI currUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String path;
        if (currUri != null && ( path = currUri.getPath()).startsWith(Xxl.GATEWAY_XXL_EXEC_PATH)){
            URI uri = UriComponentsBuilder.fromUri(currUri)
                    .port(currUri.getPort() + Xxl.GATEWAY_XXL_EXEC_PORT_OFFSET)
                    .replacePath(path.replaceFirst(Xxl.GATEWAY_XXL_EXEC_PATH,""))
                    .build(true)
                    .toUri();
            log.debug("=====> xxl-job job trigger address :{}" ,uri);
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,uri);
        }
        return chain.filter(exchange);
    }

}
