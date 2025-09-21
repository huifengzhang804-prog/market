package com.medusa.gruul.common.fastjson2.codec;

import cn.hutool.core.date.DatePattern;
import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.YearMonth;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class YearMonthCodec implements ObjectStringCodec<YearMonth> {


    @Override
    public String encodeToStr(YearMonth data) {
        return data.format(DatePattern.NORM_MONTH_FORMATTER);
    }

    @Override
    public YearMonth decodeStr(String dataStr) {
        return YearMonth.parse(dataStr, DatePattern.NORM_MONTH_FORMATTER);
    }
}
