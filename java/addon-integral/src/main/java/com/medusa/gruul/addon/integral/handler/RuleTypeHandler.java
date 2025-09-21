package com.medusa.gruul.addon.integral.handler;

import com.medusa.gruul.addon.integral.model.enums.GainRuleType;

import java.lang.annotation.*;

/**
 * 积分行为处理器注解
 *
 * @author xiaoq
 * @Description RuleTypeHandler.java
 * @date 2023-02-06 16:11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuleTypeHandler {
    GainRuleType value();
}
