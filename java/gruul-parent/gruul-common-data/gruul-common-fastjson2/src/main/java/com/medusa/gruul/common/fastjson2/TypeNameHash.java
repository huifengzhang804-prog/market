package com.medusa.gruul.common.fastjson2;

import cn.hutool.core.map.WeakConcurrentMap;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.util.Fnv;
import com.alibaba.fastjson2.util.TypeUtils;

import java.util.Map;

/**
 * type name hash
 *
 * @author 张治保
 * date 2023/5/18
 */
public record TypeNameHash(long typeNameHash, byte[] typeNameJSONB) {
    public static final Map<Class<?>, TypeNameHash> TYPE_NAME_MAP = new WeakConcurrentMap<>();

    public static TypeNameHash get(Class<?> clazz) {
        return TYPE_NAME_MAP.computeIfAbsent(
                clazz,
                cls -> {
                    String typeName = TypeUtils.getTypeName(cls);
                    return new TypeNameHash(Fnv.hashCode64(typeName), JSONB.toBytes(typeName));
                }
        );
    }

}
