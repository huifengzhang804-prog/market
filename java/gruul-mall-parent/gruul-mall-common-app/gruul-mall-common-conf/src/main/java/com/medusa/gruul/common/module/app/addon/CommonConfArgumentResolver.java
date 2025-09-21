package com.medusa.gruul.common.module.app.addon;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author 张治保
 * @since 2024/9/6
 */
@RequiredArgsConstructor
public class CommonConfArgumentResolver implements HandlerMethodArgumentResolver {

    private final CommonConfigStrategyFactory commonConfigStrategyFactory;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getContainingClass().equals(CommonConfigController.class) && parameter.hasParameterAnnotation(Config.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) (webRequest.getNativeRequest());
        String requestURI = nativeRequest.getRequestURI();
        //从 requestUri种取出@PathVariable的值
        String type = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        CommonConfigService<?> configService = commonConfigStrategyFactory.execute(type, null);
        return JSON.parseObject(nativeRequest.getInputStream(), configService.configType());
    }
}
