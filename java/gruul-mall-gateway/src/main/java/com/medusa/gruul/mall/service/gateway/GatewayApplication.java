package com.medusa.gruul.mall.service.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.Error;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;


/**
 * @author xiaoq
 * @since 2.24
 */
@SpringBootApplication
public class GatewayApplication {
    private static final String STOMP_INFO_FULL_PATH = "/gruul-mall-carrier-pigeon/pigeon/stomp/info";

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }


    /**
     * 跨域配置 只在开发/测试环境生效
     */
    @Bean
    @Profile({"dev", "test"})
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //1.配置跨域
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                PathContainer path = exchange.getRequest().getPath().pathWithinApplication();
                if (STOMP_INFO_FULL_PATH.equalsIgnoreCase(path.value())) {
                    return null;
                }
                return config;
            }
        };
        return new CorsWebFilter(source);
    }

    /**
     * 自定义熔断限流异常处理器
     *
     * @return BlockRequestHandler
     * @see BlockException
     */
    @Bean
    public BlockRequestHandler blockRequestHandler() {
        return (exchange, error) -> ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Result.failed(throwable2Error(error))));
    }

    /**
     * 异常转换
     *
     * @param error 异常
     * @return 错误码
     */
    private Error throwable2Error(Throwable error) {
        if (error instanceof FlowException) {
            return SystemCode.SENTINEL_FLOW_BLOCK;
        }
        if (error instanceof ParamFlowException) {
            return SystemCode.SENTINEL_PARAM_FLOW_BLOCK;
        }
        if (error instanceof DegradeException) {
            return SystemCode.SENTINEL_DEGRADE_BLOCK;
        }
        if (error instanceof SystemBlockException) {
            return SystemCode.SENTINEL_SYSTEM_BLOCK;
        }
        if (error instanceof AuthorityException) {
            return SystemCode.SENTINEL_AUTHORITY_BLOCK;
        }
        return SystemCode.SYSTEM_BUSY;
    }
}
