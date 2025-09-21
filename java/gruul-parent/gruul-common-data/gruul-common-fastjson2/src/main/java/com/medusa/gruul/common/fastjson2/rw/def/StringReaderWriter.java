package com.medusa.gruul.common.fastjson2.rw.def;

import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public class StringReaderWriter<T> implements ReaderWriter<T> {

    private final Codec<T, String> codec;
    private final Reader<T> reader;
    private final Writer<T> writer;


    public StringReaderWriter(Codec<T, String> codec) {
        this.codec = codec;
        this.reader = jsonReader -> codec.decode(jsonReader.readString());
        this.writer = (jsonWriter, value) -> jsonWriter.writeString(codec.encode(value));
    }


    @Override
    public Codec<T, ?> codec() {
        return codec;
    }

    @Override
    public ObjectReader<T> reader() {
        return reader;
    }

    @Override
    public ObjectWriter<T> writer() {
        return writer;
    }

}
