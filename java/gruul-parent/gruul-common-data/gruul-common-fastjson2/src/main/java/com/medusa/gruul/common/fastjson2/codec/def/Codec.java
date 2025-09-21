package com.medusa.gruul.common.fastjson2.codec.def;

import cn.hutool.core.util.TypeUtil;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface Codec<T, R> extends Encode<T, R>, Decode<T, R> {


    /**
     * 获取被转码的类 也就是序列化前的类
     *
     * @return 目标类
     */
    @SuppressWarnings("unchecked")
    default Class<T> encodeClass() {
        return (Class<T>) TypeUtil.getTypeArgument(this.getClass(), 0);
    }

    /**
     * 获取被解码的类 也就是序列化后的类
     *
     * @return 返回类
     */
    @SuppressWarnings("unchecked")
    default Class<R> decodeClass() {
        return (Class<R>) TypeUtil.getTypeArgument(this.getClass(), 1);
    }


}
