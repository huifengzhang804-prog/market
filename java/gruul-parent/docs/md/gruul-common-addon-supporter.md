# com.medusa.gruul.common.addon.supporter.AddonSupporterAutoconfigure

**文件路径**: `addon\supporter\AddonSupporterAutoconfigure.java`

## 代码文档
///
@author 张治保
date 2022/2/18
 
///


## 文件结构
```
项目根目录
└── addon\supporter
    └── AddonSupporterAutoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter;

import com.medusa.gruul.common.addon.supporter.sacnner.AddonSupporterRegistrar;
import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * date 2022/2/18
 */

@Import({
        AddonSupporterRegistrar.class
})
public class AddonSupporterAutoconfigure {
}

```

# com.medusa.gruul.common.addon.supporter.AddonSupporterI18NLoader

**文件路径**: `addon\supporter\AddonSupporterI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── addon\supporter
    └── AddonSupporterI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class AddonSupporterI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/addonSupporter");
	}
}

```

# com.medusa.gruul.common.addon.supporter.annotation.AddonMethod

**文件路径**: `supporter\annotation\AddonMethod.java`

## 代码文档
///
执行链检查器 注册注解
方法增强

@author 张治保
2022/02/19
@
 
///

///
执行器返回类型

@return 执行器返回类型
	 
///

///
第一个参数是否作为调用过滤条件 会调用arg1的toString方法 作为查找key的后缀
	 
///


## 文件结构
```
项目根目录
└── supporter\annotation
    └── AddonMethod.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.annotation;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 执行链检查器 注册注解
 * 方法增强
 *
 * @author 张治保
 * 2022/02/19
 * @
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Indexed
@Documented
@Inherited
public @interface AddonMethod {

	/**
	 * 执行器返回类型
	 *
	 * @return 执行器返回类型
	 */
	Class<?> returnType();

	/**
	 * 第一个参数是否作为调用过滤条件 会调用arg1的toString方法 作为查找key的后缀
	 */
	boolean arg1Filter() default false;

}

```

# com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter

**文件路径**: `supporter\annotation\AddonSupporter.java`

## 代码文档
///
执行链检查器 注册注解
方法增强

@author 张治保
2022/02/19
@
 
///

///
插件驱动id 全局唯一

@return 驱动id
     
///

///
服务名 为空则直接取当前服务服务名
     
///


## 文件结构
```
项目根目录
└── supporter\annotation
    └── AddonSupporter.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.annotation;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 执行链检查器 注册注解
 * 方法增强
 *
 * @author 张治保
 * 2022/02/19
 * @
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Indexed
@Documented
@Inherited
public @interface AddonSupporter {

    /**
     * 插件驱动id 全局唯一
     *
     * @return 驱动id
     */
    String id();

    /**
     * 服务名 为空则直接取当前服务服务名
     */
    String service() default "";
}

```

# com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper

**文件路径**: `supporter\helper\AddonSupporterHelper.java`

## 代码文档
///
插件执行器 工具

@author 张治保
date 2022/9/15
 
///

///
检查该方法是否存在插件

@param clazz  插件支持器类
@param func   函数
@param filter 过滤器 用于过滤插件 可以为空
@return 是否存在插件
     
///

///
获取该方法的方法名

@param func 函数
@return 方法名
     
///

///
从缓存中获取该方法的lambda表达式的序列化对象

@param func 函数
@return lambda表达式的序列化对象
     
///

///
反射获取该方法的lambda表达式的序列化对象

@param func lambda 函数
@return lambda表达式的序列化对象
     
///

///
根据class获取AddonSupporter缓存

@param clazz 本地缓存的插件支持器数据
     
///

///
获取对应插件支持器方法是否 有插件存在
     
///

///
获取对应方法的插件缓存的完整key

@param addonSupporter 注解配置
@param methodName     方法名
@param filter         插件过滤器 可以为null
@return 完整缓存key
     
///

///
尝试从spring容器中获取插件实现对象

@param beanFactory bean工厂
@param beanName    bean name
@return 获取的bean 可能为空
     
///

///
获取插件定义信息

@param cacheKey 缓存key
@return 可能为null的插件定义信息
     
///

///
执行所有插件 并获取执行结果

@param scannerContext 扫描器上下文
@param cacheKey       当前服务服务名
@param returnType     返回值类型
@param arguments      入参列表
@return 插件执行结果
     
///

///
获取单个插件执行结果

@param scannerContext  扫描器上下文
@param addonDefinition 插件定义信息
@param returnType      插件返回值类型
@param arguments       入参列表
@return 执行结果 可能为空
     
///

///
字符串参数类型转Class[]

@param strParamTypes 字符串形式的参数类型
@return Class[]
     
///

///
判断是否 不需要执行插件 不需要则直接跳过

@param paramOption   动态入参
@param conditionPath 参数路径
@return 是否 不需要执行插件
@deprecated
     
///

///
上下文初始化

@param scannerContext 插件上下文
     
///


## 文件结构
```
项目根目录
└── supporter\helper
    └── AddonSupporterHelper.java
```

## 完整代码
```java
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
                                                        );
                                            } catch (NoSuchMethodException | IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                );
                                if (log.isDebugEnabled()) {
                                    log.debug("执行插件 \n{}", methodHandle);
                                }
                                try {
                                    return methodHandle.invoke(arguments);
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

```

# com.medusa.gruul.common.addon.supporter.helper.IAddon

**文件路径**: `supporter\helper\IAddon.java`

## 代码文档
///
@author 张治保
date 2023/2/2
 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <T1>   插件函数方法参数1类型
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <T1>   插件函数方法参数1类型
@param <T2>   插件函数方法参数2类型
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <T1>   插件函数方法参数1类型
@param <T2>   插件函数方法参数2类型
@param <T3>   插件函数方法参数3类型
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <T1>   插件函数方法参数1类型
@param <T2>   插件函数方法参数2类型
@param <T3>   插件函数方法参数3类型
@param <T4>   插件函数方法参数4类型
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func 插件函数方法
@return 是否存在插件
	 
///

///
检查该方法是否存在插件

@param func   插件函数方法
@param filter 过滤器 用于过滤插件 可以为空
@param <T1>   插件函数方法参数1类型
@param <T2>   插件函数方法参数2类型
@param <T3>   插件函数方法参数3类型
@param <T4>   插件函数方法参数4类型
@param <T5>   插件函数方法参数5类型
@param <R>    插件函数方法返回值类型
@return 是否存在插件
	 
///


## 文件结构
```
项目根目录
└── supporter\helper
    └── IAddon.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.helper;

import io.vavr.*;

/**
 * @author 张治保
 * date 2023/2/2
 */
public interface IAddon {

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default boolean existed(Runnable func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @return 是否存在插件
	 */
	default boolean existed(Runnable func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <R> boolean existed(Function0<R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <R> boolean existed(Function0<R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <T1, R> boolean existed(Function1<T1, R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <T1>   插件函数方法参数1类型
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <T1, R> boolean existed(Function1<T1, R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <T1, T2, R> boolean existed(Function2<T1, T2, R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <T1>   插件函数方法参数1类型
	 * @param <T2>   插件函数方法参数2类型
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <T1, T2, R> boolean existed(Function2<T1, T2, R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, R> boolean existed(Function3<T1, T2, T3, R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <T1>   插件函数方法参数1类型
	 * @param <T2>   插件函数方法参数2类型
	 * @param <T3>   插件函数方法参数3类型
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, R> boolean existed(Function3<T1, T2, T3, R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, T4, R> boolean existed(Function4<T1, T2, T3, T4, R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <T1>   插件函数方法参数1类型
	 * @param <T2>   插件函数方法参数2类型
	 * @param <T3>   插件函数方法参数3类型
	 * @param <T4>   插件函数方法参数4类型
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, T4, R> boolean existed(Function4<T1, T2, T3, T4, R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func 插件函数方法
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, T4, T5, R> boolean existed(Function5<T1, T2, T3, T4, T5, R> func) {
		return this.existed(func, null);
	}

	/**
	 * 检查该方法是否存在插件
	 *
	 * @param func   插件函数方法
	 * @param filter 过滤器 用于过滤插件 可以为空
	 * @param <T1>   插件函数方法参数1类型
	 * @param <T2>   插件函数方法参数2类型
	 * @param <T3>   插件函数方法参数3类型
	 * @param <T4>   插件函数方法参数4类型
	 * @param <T5>   插件函数方法参数5类型
	 * @param <R>    插件函数方法返回值类型
	 * @return 是否存在插件
	 */
	default <T1, T2, T3, T4, T5, R> boolean existed(Function5<T1, T2, T3, T4, T5, R> func, String filter) {
		return AddonSupporterHelper.existed(this.getClass(), func, filter);
	}


}

```

# com.medusa.gruul.common.addon.supporter.helper.LazyCache

**文件路径**: `supporter\helper\LazyCache.java`

## 代码文档
///
懒加载缓存

@author 张治保
 
///

///
插件接口定义信息缓存
     
///

///
单体方法缓存
     
///

///
supporter注解缓存
     
///

///
序列化lambda函数缓存
     
///


## 文件结构
```
项目根目录
└── supporter\helper
    └── LazyCache.java
```

## 完整代码
```java
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
```

# com.medusa.gruul.common.addon.supporter.invoker.DefaultAddonSupporterMethodInvoker

**文件路径**: `supporter\invoker\DefaultAddonSupporterMethodInvoker.java`

## 代码文档
///
@author 张治保
date 2022/9/16
 
///

///
扫描上下文
     
///

///
addonSupporter注解
     
///

///
addonMethod注解
     
///

///
key生成函数
     
///

///
args生成函数
     
///

///
渲染并设置func函数 加锁 double check

@param methodName 方法名
@param methodArgs 方法参数
     
///

///
调用支持者方法

@param cacheKey 缓存key
@param args     参数
@return 结果
     
///


## 文件结构
```
项目根目录
└── supporter\invoker
    └── DefaultAddonSupporterMethodInvoker.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.invoker;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2022/9/16
 */
@RequiredArgsConstructor
public class DefaultAddonSupporterMethodInvoker implements IAddonSupporterMethodInvoker {

    /**
     * 扫描上下文
     */
    private final ScannerContext scannerContext;

    /**
     * addonSupporter注解
     */
    private final AddonSupporter addonSupporter;

    /**
     * addonMethod注解
     */
    private final AddonMethod addonMethod;

    /**
     * key生成函数
     */
    private volatile Function<Object[], String> keyFunc;
    /**
     * args生成函数
     */
    private volatile Function<Object[], Object[]> argsFunc;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        args = args == null ? new Object[0] : args;
        if (keyFunc != null) {
            return addonSupporterMethodInvoke(keyFunc.apply(args), argsFunc.apply(args));
        }
        syncRenderFunc(method.getName(), args);
        return addonSupporterMethodInvoke(keyFunc.apply(args), argsFunc.apply(args));
    }

    /**
     * 渲染并设置func函数 加锁 double check
     *
     * @param methodName 方法名
     * @param methodArgs 方法参数
     */
    public synchronized void syncRenderFunc(String methodName, Object[] methodArgs) {
        if (keyFunc != null) {
            return;
        }
        int length;
        if (methodArgs == null || (length = methodArgs.length) < 1 || !addonMethod.arg1Filter()) {
            String keyPrefix = AddonSupporterHelper.getCacheKey(addonSupporter, methodName, null);
            keyFunc = args -> keyPrefix;
            argsFunc = args -> args;
            return;
        }
        String keyPrefix = AddonSupporterHelper.getCacheKey(addonSupporter, methodName, null);
        keyFunc = args -> RedisUtil.key(keyPrefix, args[0].toString());
        argsFunc = args -> {
            Object[] trueArgs = new Object[length - 1];
            if (length > 1) {
                System.arraycopy(args, 1, trueArgs, 0, length - 1);
            }
            return trueArgs;
        };

    }

    /**
     * 调用支持者方法
     *
     * @param cacheKey 缓存key
     * @param args     参数
     * @return 结果
     */
    public Object addonSupporterMethodInvoke(String cacheKey, Object[] args) {
        return AddonSupporterHelper.addonsInvokedResult(
                scannerContext,
                cacheKey,
                addonMethod.returnType(),
                args
        );
    }

}

```

# com.medusa.gruul.common.addon.supporter.invoker.IAddonSupporterMethodInvoker

**文件路径**: `supporter\invoker\IAddonSupporterMethodInvoker.java`

## 代码文档
///
插件驱动执行器
 
///

///
执行方法

@param proxy  代理对象
@param method 执行方法
@param args   入参
@return 执行结果
@throws Throwable 异常
     
///


## 文件结构
```
项目根目录
└── supporter\invoker
    └── IAddonSupporterMethodInvoker.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.invoker;

import java.lang.reflect.Method;

/**
 * 插件驱动执行器
 */
interface IAddonSupporterMethodInvoker {
    /**
     * 执行方法
     *
     * @param proxy  代理对象
     * @param method 执行方法
     * @param args   入参
     * @return 执行结果
     * @throws Throwable 异常
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
```

# com.medusa.gruul.common.addon.supporter.proxy.AddonSupporterFactoryBean

**文件路径**: `supporter\proxy\AddonSupporterFactoryBean.java`

## 代码文档
///
插件驱动器 factoryBean

@author 张治保
date 2022/9/16
 
///


## 文件结构
```
项目根目录
└── supporter\proxy
    └── AddonSupporterFactoryBean.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.proxy;

import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.SmartFactoryBean;

/**
 * 插件驱动器 factoryBean
 *
 * @author 张治保
 * date 2022/9/16
 */
@RequiredArgsConstructor
public class AddonSupporterFactoryBean<T> implements SmartFactoryBean<T> {

    private final Class<T> addonSupporterInterface;
    @Setter
    private ScannerContext scannerContext;


    @Override
    public T getObject() {
        return AddonSupporterProxyFactory.newInstance(scannerContext, addonSupporterInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.addonSupporterInterface;
    }
}

```

# com.medusa.gruul.common.addon.supporter.proxy.AddonSupporterProxy

**文件路径**: `supporter\proxy\AddonSupporterProxy.java`

## 代码文档
///
代理类 参考MapperProxy

@author 张治保
date 2022/9/16
 
///


## 文件结构
```
项目根目录
└── supporter\proxy
    └── AddonSupporterProxy.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.proxy;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.common.addon.supporter.invoker.DefaultAddonSupporterMethodInvoker;
import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理类 参考MapperProxy
 *
 * @author 张治保
 * date 2022/9/16
 */
public class AddonSupporterProxy<T> implements InvocationHandler, Serializable {

    @Serial
    private static final long serialVersionUID = -4724728452352L;
    private final ScannerContext scannerContext;
    @Getter
    private final Class<T> addonSupporterInterface;

    private final Map<Method, DefaultAddonSupporterMethodInvoker> addonSupporterMethodCache = new ConcurrentHashMap<>();


    public AddonSupporterProxy(ScannerContext scannerContext, Class<T> addonSupporterInterface) {
        this.scannerContext = scannerContext;
        this.addonSupporterInterface = addonSupporterInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return addonSupporterMethod(method).invoke(proxy, method, args);
    }

    private DefaultAddonSupporterMethodInvoker addonSupporterMethod(Method method) {
        return addonSupporterMethodCache.computeIfAbsent(
                method,
                m -> new DefaultAddonSupporterMethodInvoker(
                        scannerContext,
                        AddonSupporterHelper.getAddonSupporter(addonSupporterInterface),
                        getAnno(m)
                )
        );
    }

    private AddonMethod getAnno(Method method) {
        AddonMethod annotation = AnnotationUtils.findAnnotation(method, AddonMethod.class);
        if (annotation == null) {
            return new AddonMethod() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return AddonMethod.class;
                }

                @Override
                public Class<?> returnType() {
                    return method.getReturnType();
                }

                @Override
                public boolean arg1Filter() {
                    return false;
                }

            };
        }
        return annotation;
    }
}

```

# com.medusa.gruul.common.addon.supporter.proxy.AddonSupporterProxyFactory

**文件路径**: `supporter\proxy\AddonSupporterProxyFactory.java`

## 代码文档
///
插件驱动器 代理 工厂

@author 张治保
date 2022/9/16
 
///

///
实力化代理对象

@param addonSupporterProxy 代理类
@param <T>                 类行泛型
@return 代理对象实例
     
///

///
实力化代理对象

@param scannerContext          插件扫描程序应用上下文
@param addonSupporterInterface 代理对象接口类
@param <T>                     类行泛型
@return 代理对象实例
     
///


## 文件结构
```
项目根目录
└── supporter\proxy
    └── AddonSupporterProxyFactory.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.proxy;


import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;

import java.lang.reflect.Proxy;

/**
 * 插件驱动器 代理 工厂
 *
 * @author 张治保
 * date 2022/9/16
 */
public interface AddonSupporterProxyFactory {


    /**
     * 实力化代理对象
     *
     * @param addonSupporterProxy 代理类
     * @param <T>                 类行泛型
     * @return 代理对象实例
     */
    @SuppressWarnings("unchecked")
    static <T> T newInstance(AddonSupporterProxy<T> addonSupporterProxy) {
        Class<T> addonSupporterInterface = addonSupporterProxy.getAddonSupporterInterface();
        return (T) Proxy.newProxyInstance(addonSupporterInterface.getClassLoader(), new Class[]{addonSupporterInterface}, addonSupporterProxy);
    }

    /**
     * 实力化代理对象
     *
     * @param scannerContext          插件扫描程序应用上下文
     * @param addonSupporterInterface 代理对象接口类
     * @param <T>                     类行泛型
     * @return 代理对象实例
     */
    static <T> T newInstance(ScannerContext scannerContext, Class<T> addonSupporterInterface) {
        return AddonSupporterProxyFactory.newInstance(new AddonSupporterProxy<>(scannerContext, addonSupporterInterface));
    }
}

```

# com.medusa.gruul.common.addon.supporter.sacnner.AddonSupporterRegistrar

**文件路径**: `supporter\sacnner\AddonSupporterRegistrar.java`

## 代码文档
///
扫描插件接口 并注册未动态代理对象  参考 mybatis AutoConfiguredMapperScannerRegistrar

@author 张治保
 
///


## 文件结构
```
项目根目录
└── supporter\sacnner
    └── AddonSupporterRegistrar.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.sacnner;


import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 扫描插件接口 并注册未动态代理对象  参考 mybatis AutoConfiguredMapperScannerRegistrar
 *
 * @author 张治保
 */
@Slf4j
public class AddonSupporterRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {
    private static final String SERVICE_SINGLE_KEY_IN_ENVIRONMENT = "gruul.single";
    private static final String SERVICE_NAME_KEY_IN_ENVIRONMENT = "spring.application.name";
    private final ScannerContext scannerContext = new ScannerContext();
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanning"), "@AddonSupporter");
        ClassPathAddonSupporterScanner scanner = new ClassPathAddonSupporterScanner(registry, scannerContext);
        try {
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            List<String> packages = AutoConfigurationPackages.get(scannerContext.getBeanFactory());
            scanner.addIncludeFilter(new AnnotationTypeFilter(AddonSupporter.class));
            scanner.doScan(packages.toArray(new String[0]));
        } catch (IllegalStateException ex) {
            log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanned.nothing"), I18N.msg("addon.supporter.not.worked"), ex);
        }
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.scannerContext.setBeanFactory(beanFactory);
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.scannerContext.setClassLoader(classLoader);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        boolean isSingle = environment.getProperty(SERVICE_SINGLE_KEY_IN_ENVIRONMENT, Boolean.class, false);
        this.scannerContext.setSingleApplication(isSingle);
        this.scannerContext.setServiceName(environment.getProperty(SERVICE_NAME_KEY_IN_ENVIRONMENT));
        AddonSupporterHelper.setContext(this.scannerContext);
    }
}
```

# com.medusa.gruul.common.addon.supporter.sacnner.ClassPathAddonSupporterScanner

**文件路径**: `supporter\sacnner\ClassPathAddonSupporterScanner.java`

## 代码文档
///
参考 ClassPathMapperScanner

@author 张治保
date 2022/9/16
 
///

///
调用将搜索并注册所有候选人的父搜索。然后对注册的对象进行后处理以将它们设置为 AddonSupporterFactoryBean
     
///

///
处理bean定义信息

@param beanDefinitionHolders 扫描到的bean定义信息
     
///


## 文件结构
```
项目根目录
└── supporter\sacnner
    └── ClassPathAddonSupporterScanner.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.sacnner;

import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.proxy.AddonSupporterFactoryBean;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 参考 ClassPathMapperScanner
 *
 * @author 张治保
 * date 2022/9/16
 */
@Slf4j
public class ClassPathAddonSupporterScanner extends ClassPathBeanDefinitionScanner {

    private final ScannerContext scannerContext;

    public ClassPathAddonSupporterScanner(BeanDefinitionRegistry registry, ScannerContext scannerContext) {
        super(registry);
        this.scannerContext = scannerContext;
    }

    /**
     * 调用将搜索并注册所有候选人的父搜索。然后对注册的对象进行后处理以将它们设置为 AddonSupporterFactoryBean
     */
    @Override
    @NonNull
    public Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanned.nothing"), Arrays.toString(basePackages));
            }
        } else {
            this.processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    /**
     * 处理bean定义信息
     *
     * @param beanDefinitionHolders 扫描到的bean定义信息
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        beanDefinitionHolders.forEach(
                holder -> {
                    AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();
                    //添加构造方法参数
                    ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
                    String originalClassName = beanDefinition.getBeanClassName();
                    constructorArgumentValues.addGenericArgumentValue(Objects.requireNonNull(originalClassName));
                    //添加属性参数
                    beanDefinition.getPropertyValues().add("scannerContext", this.scannerContext);
                    beanDefinition.setBeanClass(AddonSupporterFactoryBean.class);
                    log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.working"), originalClassName);
                }
        );
        log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.all.working"), "");
    }

    @Override
    protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && !metadata.isAnnotation() && metadata.isAnnotated(AddonSupporter.class.getCanonicalName());
    }
}

```

# com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext

**文件路径**: `supporter\sacnner\ScannerContext.java`

## 代码文档
///
扫描器上下文

@author 张治保
date 2022/9/16
 
///

///
是否是单体应用
     
///

///
当前服务名称
     
///

///
bean 工厂
     
///

///
bean 类加载器
     
///


## 文件结构
```
项目根目录
└── supporter\sacnner
    └── ScannerContext.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.supporter.sacnner;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.BeanFactory;

/**
 * 扫描器上下文
 *
 * @author 张治保
 * date 2022/9/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScannerContext {

    /**
     * 是否是单体应用
     */
    private boolean isSingleApplication;

    /**
     * 当前服务名称
     */
    private String serviceName;

    /**
     * bean 工厂
     */
    private BeanFactory beanFactory;

    /**
     * bean 类加载器
     */
    private ClassLoader classLoader;
}

```

