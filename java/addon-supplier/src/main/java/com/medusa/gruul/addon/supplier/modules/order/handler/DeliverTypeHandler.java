package com.medusa.gruul.addon.supplier.modules.order.handler;

import com.medusa.gruul.order.api.enums.DeliverType;

import java.lang.annotation.*;

/**
 * 配送方式 处理器 注解
 *
 * @author 张治保
 * date 2023/7/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DeliverTypeHandler {

    /**
     * 发货方式
     *
     * @return DeliverType
     */
    DeliverType value();
}
