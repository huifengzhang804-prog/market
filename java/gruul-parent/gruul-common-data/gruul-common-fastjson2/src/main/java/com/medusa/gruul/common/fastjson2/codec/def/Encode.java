package com.medusa.gruul.common.fastjson2.codec.def;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Encode<T, R> {
    /**
     * 序列化
     *
     * @param data 序列化前数据
     * @return 序列化后的数据
     */
    R encode(T data);
}
