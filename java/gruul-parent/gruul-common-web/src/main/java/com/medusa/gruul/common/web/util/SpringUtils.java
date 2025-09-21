package com.medusa.gruul.common.web.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 张治保
 * date 2021/11/29
 * @deprecated 请使用 {@link SpringUtil} 替代 全部调整完毕移除这个类
 */
@Slf4j
@Deprecated(since = "2025/05/27")
public class SpringUtils {
    private SpringUtils() {
    }

    public static <A extends Annotation> Map<String, Object> getBeansWithAnnotation(Class<A> annoClass) {
        return SpringUtil.getApplicationContext().getBeansWithAnnotation(annoClass);
    }

    /**
     * 根据注解及注解的值获取 对应的Bean
     * 要求 注解必须包含方法value 且为空参
     *
     * @deprecated 请使用 {@link AbstractStrategyFactory} 替代
     */
    @Deprecated(since = "2024/05/27")
    @SuppressWarnings("unchecked")
    public static <B, A extends Annotation, E> B getBean(Class<A> annoClass, E value) {
        Objects.requireNonNull(annoClass, "annotation class cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        Map<String, Object> beans = getBeansWithAnnotation(annoClass);
        Method valueMethod;
        try {
            valueMethod = annoClass.getDeclaredMethod("value");
        } catch (NoSuchMethodException e) {
            log.error(annoClass.getSimpleName() + "annotation dont have value method");
            throw SystemCode.FAILURE.exception();
        }
        valueMethod.setAccessible(true);
        Optional<B> any = beans.values().stream().filter(
                bean -> {
                    A anno = Optional.ofNullable(
                            bean.getClass().getAnnotation(annoClass)
                    ).orElse(
                            bean.getClass().getSuperclass().getAnnotation(annoClass)
                    );
                    if (anno == null) {
                        log.error("bean {} dont have annotation {}", bean.getClass().getSimpleName(), annoClass.getSimpleName());
                        return false;
                    }
                    Object invoke;
                    try {
                        invoke = valueMethod.invoke(anno);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        return false;
                    }
                    try {
                        if (invoke.getClass().isArray()) {
                            E[] es = (E[]) invoke;
                            return ArrayUtil.contains(es, value);
                        } else {
                            E e = (E) invoke;
                            return value == e || value.equals(e);
                        }
                    } catch (Exception ignore) {
                        log.error("annotation value type error");
                        return false;
                    }
                }
        ).map(bean -> (B) bean).findAny();
        return any.orElseGet(
                () -> {
                    log.error("cannot find correct bean from context:{}", value.getClass());
                    throw SystemCode.FAILURE.exception();
                }
        );
    }
}
