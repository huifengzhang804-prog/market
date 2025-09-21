package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalTimeCodec implements ObjectStringCodec<LocalTime> {

    @Override
    public LocalTime decodeStr(String dataStr) {
        return dataStr.length() == FastJson2.NO_SECONDS_TIME_PATTEN.length() ?
                LocalTime.parse(dataStr, FastJson2.NO_SECONDS_TIME_FORMATTER) :
                LocalTime.parse(dataStr, FastJson2.TIME_FORMATTER)
                ;
    }

    @Override
    public String encodeToStr(LocalTime data) {
        return data.format(
//                data.getSecond() == 0 ?
//                        FastJson2.NO_SECONDS_TIME_FORMATTER :
                FastJson2.TIME_FORMATTER
        );
    }
}
