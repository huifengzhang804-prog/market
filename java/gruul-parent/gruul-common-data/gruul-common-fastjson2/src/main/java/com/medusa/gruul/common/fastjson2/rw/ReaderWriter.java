package com.medusa.gruul.common.fastjson2.rw;

import com.medusa.gruul.common.fastjson2.codec.DefaultCodec;
import com.medusa.gruul.common.fastjson2.rw.def.StringReaderWriter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/12/19
 */
@RequiredArgsConstructor
public enum ReaderWriter {

    /**
     * 时长
     */
    DURATION(new StringReaderWriter<>(DefaultCodec.DURATION.codec())),

    /**
     * 时间
     */
    LOCAL_TIME(new StringReaderWriter<>(DefaultCodec.LOCAL_TIME.codec())),

    /**
     * 日期
     */
    LOCAL_DATE(new StringReaderWriter<>(DefaultCodec.LOCAL_DATE.codec())),

    /**
     * 年月
     */
    YEAR_MONTH(new StringReaderWriter<>(DefaultCodec.YEAR_MONTH.codec())),

    /**
     * 日期时间
     */
    LOCAL_DATE_TIME(new StringReaderWriter<>(DefaultCodec.LOCAL_DATE_TIME.codec())),

    ;
    public final com.medusa.gruul.common.fastjson2.rw.def.ReaderWriter<?> READER_WRITER;


}
