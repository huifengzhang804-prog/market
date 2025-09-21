package com.medusa.gruul.common.security.resource.config;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.security.resource.AccessAuthorizationManager;
import com.medusa.gruul.common.security.resource.exception.ExceptionEntryPoint;
import com.medusa.gruul.common.security.resource.properties.SecurityClientProperties;
import com.medusa.gruul.common.security.resource.tool.SecurityChainConfig;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

/**
 * @author 张治保
 * date 2022/2/28
 */
@Configuration
@RequiredArgsConstructor
public class SecurityClientConfig {

    private final SecurityClientProperties resourceProperties;
    private final JwtSecurityContextRepository securityContextRepository;
    private final HandlerMapping requestMappingHandlerMapping;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Nullable List<SecurityChainConfig> securityChainConfigs) throws Exception {
        //白名单
        SecurityClientProperties.UriPermission uriPermission = resourceProperties.getUriPermission();
        //定义请求授权规则
        http.authorizeHttpRequests(
                request -> {
                    if (uriPermission.isEnable()) {
                        request.requestMatchers(uriPermission.getPermits()).permitAll()
                                .anyRequest().access(new AccessAuthorizationManager(requestMappingHandlerMapping));
                        return;
                    }
                    request.anyRequest().permitAll();
                }
        );
        //enable Frame Options
        if (!resourceProperties.isEnableFrameOptions()) {
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        }
        //定义登录登出信息
        //禁用表单登录
        ExceptionEntryPoint authenticationEntryPoint = new ExceptionEntryPoint();
        http.formLogin(AbstractHttpConfigurer::disable)
                //禁用跨域配置
                .cors(AbstractHttpConfigurer::disable)
                //禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                //
                //无状态 session
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //定义上认证信息下文处理逻辑
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                //定义异常处理
                .exceptionHandling(
                        ex -> ex.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(authenticationEntryPoint)
                )
                //ExceptionTranslationFilter 提升到SecurityContextHolderFilter前面
                .addFilterBefore(new ExceptionTranslationFilter(authenticationEntryPoint, new NullRequestCache()), SecurityContextHolderFilter.class);
        //自定义部分
        CollUtil.emptyIfNull(securityChainConfigs).forEach(config -> config.config(http));
        return http.build();
    }


}
