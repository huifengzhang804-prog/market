package com.medusa.gruul.common.redis.constant;

/**
 * redis 结果的 序列化方式
 *
 * @author 张治保
 * @since 2024/5/27
 */
public enum ResultSerialType {
    /**
     * 已key 方式反序列化
     */
    KEY,

    /**
     * 已value 方式反序列化
     */
    VALUE
}
