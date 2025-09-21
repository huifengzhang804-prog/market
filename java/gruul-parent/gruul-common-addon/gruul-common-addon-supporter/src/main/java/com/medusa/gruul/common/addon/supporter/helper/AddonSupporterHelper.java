package com.medusa.gruul.common.addon.supporter.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.addon.model.bean.AddonDefinition;
import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import com.medusa.gruul.common.dubbo.rpc.IDynamicDubbo;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.PojoUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 插件执行器 工具
 *
 * @author 张治保
 * date 2022/9/15
 */
@Slf4j
public class AddonSupporterHelper {

    private static ScannerContext scannerContext;


    /**
     * 检查该方法是否存在插件
     *
     * @param clazz  插件支持器类
     * @param func   函数
     * @param filter 过滤器 用于过滤插件 可以为空
     * @return 是否存在插件
     */
    public static boolean existed(Class<?> clazz, Object func, String filter) {
        AddonSupporter addonSupporter = AddonSupporterHelper.getAddonSupporter(clazz);
        if (addonSupporter == null) {
            throw new NullPointerException("Class Not annotated by @AddonSupporter");
        }
        return AddonSupporterHelper.existed(addonSupporter, AddonSupporterHelper.getMethodName(func), filter);
    }

    /**
     * 获取该方法的方法名
     *
     * @param func 函数
     * @return 方法名
     */
    private static String getMethodName(Object func) {
        SerializedLambda serializedLambda = AddonSupporterHelper.getSerializedLambda(func);
        return serializedLambda.getImplMethodName();
    }

    /**
     * 从缓存中获取该方法的lambda表达式的序列化对象
     *
     * @param func 函数
     * @return lambda表达式的序列化对象
     */
    public static SerializedLambda getSerializedLambda(Object func) {
        return LazyCache.SerializedLambdaCache.MAP.computeIfAbsent(func.getClass().getName(), key -> AddonSupporterHelper.serializedLambda(func));
    }

    /**
     * 反射获取该方法的lambda表达式的序列化对象
     *
     * @param func lambda 函数
     * @return lambda表达式的序列化对象
     */
    private static SerializedLambda serializedLambda(Object func) {

        Method writeReplace;
        try {
            writeReplace = func.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        writeReplace.setAccessible(true);
        //反射调用
        try {
            return (SerializedLambda) writeReplace.invoke(func);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据class获取AddonSupporter缓存
     *
     * @param clazz 本地缓存的插件支持器数据
     */
    public static AddonSupporter getAddonSupporter(Class<?> clazz) {
        return LazyCache.AddonSupporterCache.MAP.computeIfAbsent(
                clazz,
                (c) -> AnnotationUtils.findAnnotation(clazz, AddonSupporter.class)
        );
    }

    /**
     * 获取对应插件支持器方法是否 有插件存在
     */
    public static boolean existed(AddonSupporter addonSupporter, String methodName, String filter) {
        Boolean hasKey = RedisUtil.getRedisTemplate().hasKey(AddonSupporterHelper.getCacheKey(addonSupporter, methodName, filter));
        return hasKey != null && hasKey;
    }

    /**
     * 获取对应方法的插件缓存的完整key
     *
     * @param addonSupporter 注解配置
     * @param methodName     方法名
     * @param filter         插件过滤器 可以为null
     * @return 完整缓存key
     */
    public static String getCacheKey(AddonSupporter addonSupporter, String methodName, String filter) {
        String service = scannerContext.isSingleApplication() ?
                scannerContext.getServiceName() :
                StrUtil.emptyToDefault(addonSupporter.service(), scannerContext.getServiceName());
        String supporterId = addonSupporter.id();
        return StrUtil.isEmpty(filter) ?
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, service, supporterId, methodName) :
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, service, supporterId, methodName, filter);
    }

    /**
     * 尝试从spring容器中获取插件实现对象
     *
     * @param beanFactory bean工厂
     * @param beanName    bean name
     * @return 获取的bean 可能为空
     */
    public static Option<Object> getBean(BeanFactory beanFactory, String beanName) {
        try {
            return Option.of(beanFactory.getBean(beanName));
        } catch (NoSuchBeanDefinitionException exception) {
            return Option.none();
        }
    }

    /**
     * 获取插件定义信息
     *
     * @param cacheKey 缓存key
     * @return 可能为null的插件定义信息
     */
    public static Option<AddonDefinition> getAddonDefinition(String cacheKey) {
        //先读取内存缓存
        AddonDefinition definition = LazyCache.AddonSupporterDefineCache.MAP.get(cacheKey);
        //如果内存缓存存在 则直接返回
        if (definition != null) {
            return Option.of(definition);
        }
        //如果内存缓存不存在 则读取redis缓存
        Map<String, List<AddonDefinition>> addonDefinitionsMap = RedisUtil.getCacheMap(cacheKey, new TypeReference<>() {
        });
        //如果缓存不存在则直接返回 null
        if (CollUtil.isEmpty(addonDefinitionsMap)) {
            return Option.none();
        }
        //如果redis缓存存在 则取第一个
        for (List<AddonDefinition> definitions : addonDefinitionsMap.values()) {
            if (CollUtil.isEmpty(definitions)) {
                continue;
            }
            definition = definitions.get(CommonPool.NUMBER_ZERO);
            LazyCache.AddonSupporterDefineCache.MAP.put(cacheKey, definition);
        }
        return Option.of(definition);

    }

    /**
     * 执行所有插件 并获取执行结果
     *
     * @param scannerContext 扫描器上下文
     * @param cacheKey       当前服务服务名
     * @param returnType     返回值类型
     * @param arguments      入参列表
     * @return 插件执行结果
     */
    public static Object addonsInvokedResult(ScannerContext scannerContext, String cacheKey, Class<?> returnType, Object[] arguments) {
        return getAddonDefinition(cacheKey)
                .map(addonDefinition -> AddonSupporterHelper.getAddonInvokeResult(scannerContext, addonDefinition, returnType, arguments))
                .getOrElse(() -> null);
    }

    /**
     * 获取单个插件执行结果
     *
     * @param scannerContext  扫描器上下文
     * @param addonDefinition 插件定义信息
     * @param returnType      插件返回值类型
     * @param arguments       入参列表
     * @return 执行结果 可能为空
     */
    public static Object getAddonInvokeResult(ScannerContext scannerContext, AddonDefinition addonDefinition, Class<?> returnType, Object[] arguments) {
        /*
         * 参数类型
         */
        String[] paramTypes = addonDefinition.getParameterTypes();
        //单体应用
        if (scannerContext.isSingleApplication()) {
            return AddonSupporterHelper.getBean(scannerContext.getBeanFactory(), addonDefinition.getBeanName())
                    .map(
                            bean -> {
                                String methodName = addonDefinition.getMethodName();
                                String key = bean.getClass().getName() + StrPool.AT
                                        + methodName
                                        + (paramTypes == null ? StrUtil.EMPTY : String.join(StrPool.COMMA, paramTypes));
                                log.debug("尝试获取插件 \n{}", key);
                                MethodHandle methodHandle = LazyCache.SingleCache.MAP.computeIfAbsent(
                                        key,
                                        (k) -> {
                                            //支持方法重载
                                            Class<?>[] pTypes = paramTypes(paramTypes);
                                            try {
                                                //找出public方法
                                                return MethodHandles.publicLookup()
                                                        .findVirtual(
                                                                bean.getClass(),
                                                                methodName,
                                                                MethodType.methodType(returnType, pTypes)
                                                        ).bindTo(bean);
                                            } catch (NoSuchMethodException | IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                );
                                if (log.isDebugEnabled()) {
                                    log.debug("执行插件 \n{}", methodHandle);
                                }
                                try {
                                    return methodHandle.invokeWithArguments(arguments);
                                } catch (Throwable e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ).getOrElse(() -> null);
        }
        //分布式应用
        Object value = IDynamicDubbo.genericService(addonDefinition.getInterfaceName())
                .$invoke(addonDefinition.getMethodName(), paramTypes, arguments);
        if (returnType == void.class || value == null || returnType.isAssignableFrom(value.getClass())) {
            return value;
        }
        return PojoUtils.realize(value, returnType);
    }

    /**
     * 字符串参数类型转Class[]
     *
     * @param strParamTypes 字符串形式的参数类型
     * @return Class[]
     */
    public static Class<?>[] paramTypes(String[] strParamTypes) {
        //无参方法
        if (strParamTypes == null) {
            return new Class[0];
        }
        //有参方法
        Class<?>[] pTypes = new Class[strParamTypes.length];
        for (int i = 0; i < strParamTypes.length; i++) {
            try {
                pTypes[i] = Class.forName(strParamTypes[i]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return pTypes;
    }

    /**
     * 将基本类型名称转换为对应的包装类型名称
     */
    public static String convertToWrapperType(String primitiveType) {
        switch (primitiveType.toLowerCase()) {
            case "boolean":
                return "java.lang.Boolean";
            case "byte":
                return "java.lang.Byte";
            case "char":
                return "java.lang.Character";
            case "short":
                return "java.lang.Short";
            case "int":
                return "java.lang.Integer";
            case "long":
                return "java.lang.Long";
            case "float":
                return "java.lang.Float";
            case "double":
                return "java.lang.Double";
            case "void":
                return "java.lang.Void";
            default:
                // 如果不是基本类型，直接返回原值
                return primitiveType;
        }
    }

    /**
     * 判断是否 不需要执行插件 不需要则直接跳过
     *
     * @param paramOption   动态入参
     * @param conditionPath 参数路径
     * @return 是否 不需要执行插件
     * @deprecated
     */
    @Deprecated
    public static boolean dontNeedInvokeAddon(Option<JSONObject> paramOption, String conditionPath) {
        if (StrUtil.isEmpty(conditionPath)) {
            return false;
        }
        return paramOption.map(
                param -> ObjectUtil.isEmpty(param.getByPath(conditionPath))
        ).getOrElse(true);
    }

    /**
     * 上下文初始化
     *
     * @param scannerContext 插件上下文
     */
    public static void setContext(ScannerContext scannerContext) {
        AddonSupporterHelper.scannerContext = scannerContext;
    }


}
