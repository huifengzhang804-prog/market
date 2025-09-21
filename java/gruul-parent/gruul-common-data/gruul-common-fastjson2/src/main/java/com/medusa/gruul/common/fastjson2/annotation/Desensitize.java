package com.medusa.gruul.common.fastjson2.annotation;

import cn.hutool.core.util.DesensitizedUtil;

import java.lang.annotation.*;

/**
 * 数据脱敏注解 只针对 string类型字段
 *
 * @author 张治保
 * date 2023/5/17
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@JSONField(serializeUsing = DesensitizeWriter.class)
@Inherited
public @interface Desensitize {

    /**
     * 脱敏类型
     *
     * @return DesensitizedUtil.DesensitizedType
     */
    DesensitizedUtil.DesensitizedType type() default DesensitizedUtil.DesensitizedType.CHINESE_NAME;

    /**
     * 脱敏开始位置
     *
     * @return int
     */
    int start() default -1;

    /**
     * 脱敏结束位置
     *
     * @return int
     */
    int end() default -1;

    /**
     * 正则表达式 脱敏 通过正则查找到字符串，template，$1表示分组1的字符串
     */
    String regx() default "";

    /**
     * 替代模板
     */
    String template() default "";
}
