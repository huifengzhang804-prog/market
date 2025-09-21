package com.medusa.gruul.common.addon.supporter.annotation;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 执行链检查器 注册注解
 * 方法增强
 *
 * @author 张治保
 * 2022/02/19
 * @
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Indexed
@Documented
@Inherited
public @interface AddonSupporter {

    /**
     * 插件驱动id 全局唯一
     *
     * @return 驱动id
     */
    String id();

    /**
     * 服务名 为空则直接取当前服务服务名
     */
    String service() default "";
}
