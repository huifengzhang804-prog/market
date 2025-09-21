package com.medusa.gruul.common.log.annotation;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.*;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log{

    /**
     * 日志描述
     */
    String value() default "";

    /**
     * 日志级别
     */
    LogLevel level() default LogLevel.DEBUG;

    /**
     * 请求头名称
     */
    String[] headers() default {};


}
