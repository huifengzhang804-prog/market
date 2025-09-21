package com.medusa.gruul.common.fastjson2.codec.def;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Decode<T, R> {


    /**
     * 反序列化
     *
     * @param data 序列化后的数据
     * @return 反序列化后的数据
     */
    T decode(R data);
}
