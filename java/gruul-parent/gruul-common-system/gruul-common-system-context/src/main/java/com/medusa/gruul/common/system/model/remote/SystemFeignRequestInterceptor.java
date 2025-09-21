package com.medusa.gruul.common.system.model.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/2/17
 */
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class SystemFeignRequestInterceptor {

    @Bean("systemRequestInterceptor")
    public feign.RequestInterceptor requestInterceptor() {
        return template -> {
            Map<String, String> requestHeaders = HeaderRender.requestHeaders();
            requestHeaders.forEach(template::header);
        };
    }
}
