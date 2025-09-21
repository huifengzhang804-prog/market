package com.medusa.gruul.common.module.app.addon;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/9/6
 */
@Configuration
@RequiredArgsConstructor
public class CommonConfigMvcConfigure implements WebMvcConfigurer {
 
    private final CommonConfigStrategyFactory commonConfigStrategyFactory;

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CommonConfArgumentResolver(commonConfigStrategyFactory));
    }


}
