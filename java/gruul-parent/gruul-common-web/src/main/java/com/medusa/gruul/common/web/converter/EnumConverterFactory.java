package com.medusa.gruul.common.web.converter;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.fastjson2.FastJson2;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

/**
 * 枚举转换器
 *
 * @author 张治保
 * @since 2023/12/5
 */
public class EnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    public static final EnumConverterFactory INSTANCE = new EnumConverterFactory();

    @NotNull
    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        return new StringToEnum<>(targetType);
    }

    /**
     * 枚举转换器
     *
     * @param <T> 枚举类型
     * @author 张治保
     */
    @RequiredArgsConstructor
    private static class StringToEnum<T extends Enum<?>> implements Converter<String, T> {

        private final Class<T> enumType;

        @Override
        @Nullable
        public T convert(@NotNull String source) {
            if (StrUtil.isEmpty(source)) {
                return null;
            }
            return FastJson2.convert(source, enumType);
        }
    }
}
