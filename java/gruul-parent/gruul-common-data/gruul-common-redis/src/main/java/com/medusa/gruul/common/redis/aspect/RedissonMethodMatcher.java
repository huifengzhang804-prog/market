package com.medusa.gruul.common.redis.aspect;

import com.medusa.gruul.common.redis.annotation.MultiRedisson;
import com.medusa.gruul.common.redis.annotation.Redisson;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * 方法匹配器
 * 1. 方法上有 @Redisson 注解的方法
 * 2. 方法上有 @Redissons 注解的方法
 *
 * @author 张治保
 * @since 2023/12/04
 */
public class RedissonMethodMatcher extends StaticMethodMatcher {

    @Override
    public boolean matches(@NotNull Method method, @NotNull Class<?> targetClass) {
        return AnnotatedElementUtils.hasAnnotation(method, Redisson.class) || AnnotatedElementUtils.hasAnnotation(method, MultiRedisson.class);
    }
}