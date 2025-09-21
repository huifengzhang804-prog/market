package com.medusa.gruul.common.redis.annotation;

import java.lang.annotation.*;

/**
 * 批量 Redisson 联合注解
 *
 * @author 张治保
 * @since 2023/12/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface MultiRedisson {

    /**
     * Redisson 联合注解
     *
     * @return 联合注解
     */
    Redisson[] value();
}