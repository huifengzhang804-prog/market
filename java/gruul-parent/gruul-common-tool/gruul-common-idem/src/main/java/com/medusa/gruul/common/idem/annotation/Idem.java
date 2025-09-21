package com.medusa.gruul.common.idem.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口幂等性注解
 *
 * @author 张治保
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Idem {
    /**
     * 过期时间
     *
     * @return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     */
    @AliasFor("expire")
    long value() default 2000;

    /**
     * 过期时间
     *
     * @return 过期时间/单位 毫秒 默认2秒 2秒内重复提交无效
     */
    @AliasFor("value")
    long expire() default 2000;

    /**
     * 时间单位
     *
     * @return 时间单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
