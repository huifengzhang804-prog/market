package com.medusa.gruul.common.redis.annotation;


import org.intellij.lang.annotations.Language;
import org.redisson.config.Config;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * date 2021/3/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(MultiRedisson.class)
public @interface Redisson {
    /**
     * 锁名称 拼接成key前缀
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 锁名称 拼接成key前缀
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 支持 SPEl表达式
     * 批量锁每项参数名为 item {@link com.medusa.gruul.common.redis.constant.RedisConstant#BATCH_LOCK_ITEM}
     */
    @Language(value = "SpEL")
    String key() default "";

    /**
     * 尝试获取锁的等待时间 默认无限等待
     */
    long waitTime() default Long.MAX_VALUE;

    /**
     * 锁时间 如果<=0 则使用配置的lockWatchdogTimeout  默认30秒
     *
     * @see Config#getLockWatchdogTimeout()
     */
    long leaseTime() default -1;

    /**
     * 时间单位 unit the time unit
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 支持 SPEl表达式
     * 批量锁参数名，为空 则为单锁，不为空则为批量锁
     */
    @Language(value = "SpEL")
    String batchParamName() default "";

    /**
     * 支持 SPEl表达式
     * 使用锁条件  默认为空
     */
    @Language(value = "SpEL")
    String condition() default "";
}
