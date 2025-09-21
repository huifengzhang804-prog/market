package com.medusa.gruul.common.redis.aspect;

import com.medusa.gruul.common.redis.annotation.MultiRedisson;
import com.medusa.gruul.common.redis.annotation.Redisson;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Set;

/**
 * Redisson切入点
 *
 * @author 张治保
 * @since 2022/12/04
 */
public class RedissonPointcut implements Pointcut {
    private final ClassFilter classFilter = clazz -> AnnotationUtils.isCandidateClass(clazz, Set.of(MultiRedisson.class, Redisson.class));
    private final MethodMatcher methodMatcher = new RedissonMethodMatcher();

    @NotNull
    @Override
    public ClassFilter getClassFilter() {
        return classFilter;
    }

    @NotNull
    @Override
    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }
}

