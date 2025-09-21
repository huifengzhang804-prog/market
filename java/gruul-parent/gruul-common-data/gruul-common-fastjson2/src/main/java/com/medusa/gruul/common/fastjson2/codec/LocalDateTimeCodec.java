package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class LocalDateTimeCodec implements ObjectStringCodec<LocalDateTime> {

    private static final int DATE_LENGTH = FastJson2.DATE_PATTEN.length();
    private static final int NO_SECONDS_DATE_TIME_LENGTH = FastJson2.NO_SECONDS_DATETIME_PATTEN.length();


    @Override
    public String encodeToStr(LocalDateTime data) {
        return data.format(FastJson2.DATETIME_FORMATTER);
    }

    @Override
    public LocalDateTime decodeStr(String dataStr) {
        if (dataStr.length() == DATE_LENGTH) {
            LocalDate localDate = LocalDate.parse(dataStr, FastJson2.DATE_FORMATTER);
            return localDate.atStartOfDay();
        }
        if (dataStr.length() == NO_SECONDS_DATE_TIME_LENGTH) {
            return LocalDateTime.parse(dataStr, FastJson2.NO_SECONDS_DATETIME_FORMATTER);
        }
        return LocalDateTime.parse(dataStr, FastJson2.DATETIME_FORMATTER);
    }
}
