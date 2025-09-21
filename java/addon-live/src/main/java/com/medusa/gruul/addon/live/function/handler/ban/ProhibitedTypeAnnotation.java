package com.medusa.gruul.addon.live.function.handler.ban;

import com.medusa.gruul.addon.live.enums.ProhibitedType;

import java.lang.annotation.*;

/**
 * @author miskw
 * @date 2023/6/15
 * @describe 描述
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ProhibitedTypeAnnotation {
    /**
     * 禁播类型
     *
     */
    ProhibitedType value();
}
