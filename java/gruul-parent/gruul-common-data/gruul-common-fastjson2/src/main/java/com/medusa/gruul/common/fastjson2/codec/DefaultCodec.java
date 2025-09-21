package com.medusa.gruul.common.fastjson2.codec;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public enum DefaultCodec {


    /**
     * 时长
     */
    DURATION(new DurationCodec()),

    /**
     * 时间
     */
    LOCAL_TIME(new LocalTimeCodec()),

    /**
     * 日期
     */
    LOCAL_DATE(new LocalDateCodec()),

    /**
     * 年月
     */
    YEAR_MONTH(new YearMonthCodec()),

    /**
     * 日期时间
     */
    LOCAL_DATE_TIME(new LocalDateTimeCodec()),

    ;

    public static final Map<CodecClass, Codec<Object, Object>> CODEC_MAP = MapUtil.newHashMap();

    static {
        for (DefaultCodec value : DefaultCodec.values()) {
            Codec<Object, Object> curCodec = value.codec();
            Class<?> key = curCodec.encodeClass();
            CODEC_MAP.put(new CodecClass(key, curCodec.decodeClass()), curCodec);
        }
    }

    private final Codec<Object, Object> codec;

    @SuppressWarnings("unchecked")
    DefaultCodec(Codec<?, ?> codec) {
        this.codec = (Codec<Object, Object>) codec;
    }

    public static Codec<Object, Object> getCodec(Class<?> encode, Class<?> decode) {
        return CODEC_MAP.get(new CodecClass(encode, decode));
    }

    @SuppressWarnings("unchecked")
    public <T, R> Codec<T, R> codec() {
        return (Codec<T, R>) codec;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class CodecClass {

        private final Class<?> encode;
        private final Class<?> decode;

    }
}
