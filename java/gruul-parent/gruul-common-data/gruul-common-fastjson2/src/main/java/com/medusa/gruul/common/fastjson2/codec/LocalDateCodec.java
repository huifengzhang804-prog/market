package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalDateCodec implements ObjectStringCodec<LocalDate> {


    @Override
    public String encodeToStr(LocalDate data) {
        return data.format(FastJson2.DATE_FORMATTER);
    }

    @Override
    public LocalDate decodeStr(String dataStr) {
        return LocalDate.parse(dataStr, FastJson2.DATE_FORMATTER);
    }

}
