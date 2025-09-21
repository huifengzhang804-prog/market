package com.medusa.gruul.common.web.converter;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.fastjson2.codec.DefaultCodec;
import com.medusa.gruul.common.fastjson2.codec.def.Codec;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Set;

/**
 * @author 张治保
 * @since 2023/12/19
 */
public class CodecGenericConverter implements GenericConverter {
    private final Set<GenericConverter.ConvertiblePair> convertiblePairs = CollUtil.newHashSet();

    {
        DefaultCodec.CODEC_MAP.forEach(
                (clazz, codec) -> convertiblePairs.add(new GenericConverter.ConvertiblePair(clazz.getDecode(), clazz.getEncode()))
        );
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        Codec<Object, Object> codec = DefaultCodec.getCodec(targetType.getType(), sourceType.getType());
        return codec == null ? source : codec.decode(source);
    }
}
