package com.medusa.gruul.common.web.parameter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author 张治保
 * @since 2024/5/30
 */
@RequiredArgsConstructor
public class RequestBodyParamResolver implements HandlerMethodArgumentResolver {

    private static final String REQUEST_BODY_KEY = RequestBodyParamResolver.class.getName() + "_body";
    private volatile ConversionService conversionService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BodyParam.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //尝试取出上次设置的值
        JSONObject body = (JSONObject) webRequest.getAttribute(REQUEST_BODY_KEY, NativeWebRequest.SCOPE_REQUEST);
        //如果没取到 说明是第一次取值，则从输入流中取值，并设置到request中
        if (body == null) {
            //取出输入流
            HttpServletRequest nativeRequest = (HttpServletRequest) (webRequest.getNativeRequest());
            body = JSON.parseObject(nativeRequest.getInputStream());
            if (body == null) {
                throw new IllegalArgumentException("request body is null");
            }
            webRequest.setAttribute(REQUEST_BODY_KEY, body, NativeWebRequest.SCOPE_REQUEST);
        }

        BodyParam requestBodyParam = parameter.getParameterAnnotation(BodyParam.class);
        if (requestBodyParam == null) {
            throw new IllegalArgumentException("parameter annotation @RequestBodyParam is null");
        }
        //取出参数名
        String name = requestBodyParam.name();
        String value = requestBodyParam.value();
        String paramName = name;
        if (StrUtil.isBlank(name)) {
            paramName = StrUtil.isBlank(value) ? parameter.getParameterName() : value;
        }
        if (StrUtil.isBlank(paramName)) {
            throw new IllegalArgumentException("request body param name is null");
        }
        paramName = paramName.trim();
        //取出参数类型
        //尝试取出参数值
        TypeDescriptor targetType = new TypeDescriptor(parameter);

        Object object = body.get(paramName);
        if (object != null) {
            object = convert(object, targetType);
        }
        String defaultValue;
        //如果为空并且有默认值
        if (object == null && !ValueConstants.DEFAULT_NONE.equals(defaultValue = requestBodyParam.defaultValue())) {
            object = convert(defaultValue.trim(), targetType);
        }
        //如果为空并且必须 
        if (object == null && requestBodyParam.required()) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("request body param '" + paramName + "' is required");
        }
        return object;
    }

    /**
     * 参数数据类型转换 （反序列化）
     *
     * @param value      参数值
     * @param targetType 目标参数类型
     * @return 转换后的参数值
     */
    private Object convert(Object value, TypeDescriptor targetType) {
        return conversionService().convert(
                value,
                TypeDescriptor.forObject(value),
                targetType
        );
    }


    /**
     * 获取 ConversionService
     *
     * @return ConversionService
     */
    private ConversionService conversionService() {
        if (conversionService != null) {
            return conversionService;
        }
        synchronized (this) {
            if (conversionService != null) {
                return conversionService;
            }
            return conversionService = SpringUtil.getBean(ConversionService.class);
        }
    }
}
