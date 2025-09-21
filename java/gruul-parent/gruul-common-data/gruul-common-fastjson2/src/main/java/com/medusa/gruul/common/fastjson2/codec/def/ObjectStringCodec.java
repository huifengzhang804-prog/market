package com.medusa.gruul.common.fastjson2.codec.def;

import cn.hutool.core.util.StrUtil;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface ObjectStringCodec<T> extends Codec<T, String> {

    /**
     * 序列化
     *
     * @param data 序列化前数据
     * @return 序列化后的数据
     */
    @Override
    default String encode(T data) {
        return data == null ? null : encodeToStr(data);
    }


    /**
     * 反序列化
     *
     * @param dataStr 序列化后的数据
     * @return 反序列化后的数据
     */
    @Override
    default T decode(String dataStr) {
        return StrUtil.isBlank(dataStr) ? null : decodeStr(dataStr);
    }

    /**
     * 序列化成字符串
     *
     * @param data 原始数据
     * @return 序列化后的字符串
     */
    String encodeToStr(T data);

    /**
     * 反序列化字符串
     *
     * @param dataStr 序列化后的字符串
     * @return 原始数据
     */
    T decodeStr(String dataStr);


    /**
     * 获取被解码的类 也就是序列化后的类
     *
     * @return 返回类
     */
    @Override
    default Class<String> decodeClass() {
        return String.class;
    }
}
