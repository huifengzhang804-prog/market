package com.medusa.gruul.common.system.model.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/3/24
 */
@ConditionalOnClass(name = "org.springframework.http.client.ClientHttpRequestInterceptor")
public class SystemRestTemplateRequestInterceptor {

    @Bean("systemClientHttpRequestInterceptor")
    public org.springframework.http.client.ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {

            HttpHeaders headers = request.getHeaders();
            Map<String, String> requestHeaders = HeaderRender.requestHeaders();
            requestHeaders.forEach(headers::add);
            return execution.execute(request, body);
        };
    }
}
