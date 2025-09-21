package com.medusa.gruul.common.addon.supporter.helper;

import cn.hutool.core.map.WeakConcurrentMap;
import com.medusa.gruul.common.addon.model.bean.AddonDefinition;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.SerializedLambda;
import java.util.Map;

/**
 * 懒加载缓存
 *
 * @author 张治保
 */
public class LazyCache {

    /**
     * 插件接口定义信息缓存
     */
    static class AddonSupporterDefineCache {
        static final Map<String, AddonDefinition> MAP = new WeakConcurrentMap<>();
    }

    /**
     * 单体方法缓存
     */
    static class SingleCache {
        static final Map<String, MethodHandle> MAP = new WeakConcurrentMap<>();
    }

    /**
     * supporter注解缓存
     */
    static class AddonSupporterCache {
        static final Map<Class<?>, AddonSupporter> MAP = new WeakConcurrentMap<>();

    }

    /**
     * 序列化lambda函数缓存
     */
    static class SerializedLambdaCache {
        static final Map<String, SerializedLambda> MAP = new WeakConcurrentMap<>();
    }
}