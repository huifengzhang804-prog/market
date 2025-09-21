package com.medusa.gruul.common.addon.provider;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 声明为插件Bean 扫描所有使用注解 @AddonProvider 的方法
 *
 * @author 张治保
 * date 2022/2/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
public @interface AddonProviders {
}
