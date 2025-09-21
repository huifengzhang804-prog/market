package com.medusa.gruul.common.fastjson2.filter;

import cn.hutool.core.map.WeakConcurrentMap;
import com.alibaba.fastjson2.filter.ContextAutoTypeBeforeHandler;
import com.alibaba.fastjson2.util.TypeUtils;

import java.util.Map;

/**
 * auto type support filter
 * 自动类型推断 支持器
 *
 * @author 张治保
 * @since 2023/11/8
 */
public class AutoTypeSupportFilter extends ContextAutoTypeBeforeHandler {

    /**
     * 默认实例
     */
    public static final AutoTypeSupportFilter DEFAULT_INSTANCE = new AutoTypeSupportFilter();

    /**
     * class cache for name
     */
    final Map<String, Class<?>> classCache = new WeakConcurrentMap<>();


    public AutoTypeSupportFilter() {
        super(true);
    }


    public AutoTypeSupportFilter(String... acceptNames) {
        super(true, acceptNames);
    }

    public AutoTypeSupportFilter(Class<?>... classes) {
        super(true, classes);
    }


    @Override
    public Class<?> apply(String typeName, Class<?> expectClass, long features) {
        Class<?> tryLoad = super.apply(typeName, expectClass, features);
        if (tryLoad != null) {
            return tryLoad;
        }
        return loadClassDirectly(typeName);
    }


    public Class<?> loadClassDirectly(String typeName) {
        Class<?> clazz = classCache.get(typeName);
        if (clazz == null) {
            clazz = TypeUtils.getMapping(typeName);
        }
        if (clazz == null) {
            clazz = TypeUtils.loadClass(typeName);
        }
        if (clazz != null) {
            Class<?> origin = classCache.putIfAbsent(typeName, clazz);
            if (origin != null) {
                clazz = origin;
            }
        }
        return clazz;
    }

}