package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public interface ReaderWriter<T> {


    /**
     * 获取 codec 编码解码器
     *
     * @return codec 编码解码器
     */
    Codec<T, ?> codec();

    /**
     * 获取 reader 反序列化器
     *
     * @return reader 反序列化器
     */
    ObjectReader<T> reader();

    /**
     * 获取 writer 序列化器
     *
     * @return writer 序列化器
     */
    ObjectWriter<T> writer();
}
