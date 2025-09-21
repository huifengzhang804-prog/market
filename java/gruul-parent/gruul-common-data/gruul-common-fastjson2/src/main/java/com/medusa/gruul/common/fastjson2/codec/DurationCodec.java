package com.medusa.gruul.common.fastjson2.codec;

import com.medusa.gruul.common.fastjson2.codec.def.ObjectStringCodec;

import java.time.Duration;

/**
 * @author 张治保
 * @since 2023/12/19
 */
class DurationCodec implements ObjectStringCodec<Duration> {


    @Override
    public String encodeToStr(Duration data) {
        return data.toString();
    }

    @Override
    public Duration decodeStr(String dataStr) {
        return Duration.parse(dataStr);
    }

}
