package com.medusa.gruul.addon.live.function;

import com.medusa.gruul.addon.live.enums.LiveNotify;

import java.lang.annotation.*;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 描述
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface LiveNotifyAnnotation {
    LiveNotify value();
}
