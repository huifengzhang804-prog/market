# com.medusa.gruul.common.addon.provider.AddonProvider

**文件路径**: `addon\provider\AddonProvider.java`

## 代码文档
///
插件对外提供的远程调用接口
服务名 驱动id 方法名 拼接成 redis key （service:driverId:methodName）

@author 张治保
date 2022/2/21
 
///

///
服务名
	 
///

///
插件支持器id
	 
///

///
方法名
	 
///

///
插件执行顺序
	 
///

///
是否是异步调用
	 
///

///
接口类全名
	 
///

///
接口类class
	 
///

///
条件字段路径 满足条件：当该路径的值不为空时 才会调用插件
	 
///


## 文件结构
```
项目根目录
└── addon\provider
    └── AddonProvider.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;


import java.lang.annotation.*;

/**
 * 插件对外提供的远程调用接口
 * 服务名 驱动id 方法名 拼接成 redis key （service:driverId:methodName）
 *
 * @author 张治保
 * date 2022/2/21
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AddonProvider {
	/**
	 * 服务名
	 */
	String service();

	/**
	 * 插件支持器id
	 */
	String supporterId();

	/**
	 * 方法名
	 */
	String methodName();

	/**
	 * 插件执行顺序
	 */
	int order() default 0;

	/**
	 * 是否是异步调用
	 */
	boolean async() default false;

	/**
	 * 接口类全名
	 */
	String interfaceName() default "";

	/**
	 * 接口类class
	 */
	Class<?> interfaceClass() default Exception.class;


	/**
	 * 条件字段路径 满足条件：当该路径的值不为空时 才会调用插件
	 */
	String filter() default "";
}

```

# com.medusa.gruul.common.addon.provider.AddonProviderI18NLoader

**文件路径**: `addon\provider\AddonProviderI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── addon\provider
    └── AddonProviderI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class AddonProviderI18NLoader implements I18NPropertiesLoader {
	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/addonProvider");
	}
}

```

# com.medusa.gruul.common.addon.provider.AddonProviders

**文件路径**: `addon\provider\AddonProviders.java`

## 代码文档
///
声明为插件Bean 扫描所有使用注解 @AddonProvider 的方法

@author 张治保
date 2022/2/21
 
///


## 文件结构
```
项目根目录
└── addon\provider
    └── AddonProviders.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 声明为插件Bean 扫描所有使用注解 @AddonProvider 的方法
 *
 * @author 张治保
 * date 2022/2/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
public @interface AddonProviders {
}

```

# com.medusa.gruul.common.addon.provider.AddonSupportHook

**文件路径**: `addon\provider\AddonSupportHook.java`

## 代码文档
///
@author 张治保
date 2022/9/14
 
///

///
分布式
     
///

///
单体
     
///

///
插件钩子函数
     
///


## 文件结构
```
项目根目录
└── addon\provider
    └── AddonSupportHook.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.addon.model.bean.AddonDefinition;
import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.model.handler.AddonHook;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/9/14
 */
@Slf4j
public class AddonSupportHook implements ApplicationContextAware {

    private final String addonRecordKey;
    private final String addonServiceName;
    private ApplicationContext applicationContext;

    public AddonSupportHook(GlobalAppProperties globalAppProperties) {
        this.addonServiceName = globalAppProperties.getName();
        this.addonRecordKey = RedisUtil.key(AddonConst.REDIS_ADDON_PROVIDER, this.addonServiceName);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 分布式
     */
    @Bean(name = "supportKeyGenerator")
    @ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
    public ISupportKeyGenerator supportKeyGenerator() {
        return annotation -> StrUtil.isEmpty(annotation.filter()) ?
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, annotation.service(), annotation.supporterId(), annotation.methodName()) :
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, annotation.service(), annotation.supporterId(), annotation.methodName(), annotation.filter());
    }

    /**
     * 单体
     */
    @Bean(name = "supportKeyGenerator")
    @ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "true")
    public ISupportKeyGenerator supportKeyGeneratorSingle() {
        return annotation -> StrUtil.isEmpty(annotation.filter()) ?
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, addonServiceName, annotation.supporterId(), annotation.methodName()) :
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, addonServiceName, annotation.supporterId(), annotation.methodName(), annotation.filter());
    }

    /**
     * 插件钩子函数
     */
    @Bean
    public AddonHook addonHook(ISupportKeyGenerator supportKeyGenerator) {
        return new AddonHook() {
            @Override
            public void install() {
                RedisUtil.getRedisTemplate().opsForSet().add(AddonConst.ADDON_PROVIDER_REGISTER, addonServiceName);
                log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.scanning"), addonServiceName);
                Map<String, List<AddonDefinition>> addonDefinitionsMap = AddonSupportScanner.scan(applicationContext, supportKeyGenerator);
                if (CollUtil.isEmpty(addonDefinitionsMap)) {
                    log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.scan.empty"), addonServiceName);
                    return;
                }
                addonDefinitionsMap.forEach(
                        (cacheKey, addonDefinitions) -> {
                            RedisUtil.setCacheMapValue(cacheKey, addonServiceName, addonDefinitions);
                            log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.working"), addonServiceName);
                        }
                );
                RedisUtil.setCacheSet(addonRecordKey, addonDefinitionsMap.keySet());
            }

            @Override
            public void uninstall() {
            }
        };
    }


}

```

# com.medusa.gruul.common.addon.provider.AddonSupportScanner

**文件路径**: `addon\provider\AddonSupportScanner.java`

## 代码文档
///
扫描所有带 AddonSupport注解的方法 获取插件定义信息
扫描

@author 张治保
date 2022/9/14
 
///

///
扫描并获取所有的插件注册信息

@param applicationContext spring应用程序上下文
@return 所有的插件注册信息 key ->  驱动目标 最终的缓存key   value插件定义信息列表
	 
///

///
获取插件定义信息

@param beanName        ，bean名称
@param beanTargetClass bean的目标类
@param method          插件生效的方法
@param annotation      插件注解信息
@return 插件定义信息
	 
///

///
方法参数类型列表转  String 全类名参数类型列表

@param methodParameterTypes 方法参数列表
@return String 全类名参数类型列表
	 
///

///
获取插件接口全类名

@param annotation      插件注解
@param beanTargetClass bean目标类型
@return 插件接口全类名
	 
///


## 文件结构
```
项目根目录
└── addon\provider
    └── AddonSupportScanner.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.addon.model.bean.AddonDefinition;
import com.medusa.gruul.common.addon.model.enums.AddonFuncType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.i18n.I18N;
import io.vavr.control.Option;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 扫描所有带 AddonSupport注解的方法 获取插件定义信息
 * 扫描
 *
 * @author 张治保
 * date 2022/9/14
 */
public interface AddonSupportScanner {

	Class<AddonProvider> ANNOTATION_CLASS = AddonProvider.class;
	Class<? extends Annotation> ADDON_BEAN_CLASS = AddonProviders.class;

	/**
	 * 扫描并获取所有的插件注册信息
	 *
	 * @param applicationContext spring应用程序上下文
	 * @return 所有的插件注册信息 key ->  驱动目标 最终的缓存key   value插件定义信息列表
	 */
	static Map<String, List<AddonDefinition>> scan(ApplicationContext applicationContext, ISupportKeyGenerator supportKeyGenerator) {
		Map<String, Object> addonSupportBeanMap = applicationContext.getBeansWithAnnotation(ADDON_BEAN_CLASS);
		if (CollUtil.isEmpty(addonSupportBeanMap)) {
			return Collections.emptyMap();
		}
		Map<String, List<AddonDefinition>> allAddonDefinitionMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
		addonSupportBeanMap.forEach(
				(beanName, beanInstance) -> {
					//获取bean原生类
					Class<?> beanTargetClass = AopUtils.getTargetClass(beanInstance);
					Method[] methods = ReflectionUtils.getDeclaredMethods(beanTargetClass);
					for (Method method : methods) {
						Option.of(AnnotationUtils.getAnnotation(method, ANNOTATION_CLASS))
								.peek(
										annotation -> allAddonDefinitionMap.computeIfAbsent(
												supportKeyGenerator.key(annotation),
												key -> new ArrayList<>(CommonPool.NUMBER_FIFTEEN)
										).add(AddonSupportScanner.definition(beanName, beanTargetClass, method, annotation))
								);
					}
				}
		);
		return allAddonDefinitionMap;
	}

	/**
	 * 获取插件定义信息
	 *
	 * @param beanName        ，bean名称
	 * @param beanTargetClass bean的目标类
	 * @param method          插件生效的方法
	 * @param annotation      插件注解信息
	 * @return 插件定义信息
	 */
	static AddonDefinition definition(String beanName, Class<?> beanTargetClass, Method method, AddonProvider annotation) {
		return new AddonDefinition()
				.setFuncType(AddonFuncType.DUBBO)
				.setBeanName(beanName)
				.setInterfaceName(getInterfaceClassFullName(annotation, beanTargetClass))
				.setMethodName(method.getName())
				.setParameterTypes(getParameterTypes(method.getParameterTypes()))
				.setFilter(annotation.filter())
				.setOrder(annotation.order())
				.setAsync(annotation.async());
	}

	/**
	 * 方法参数类型列表转  String 全类名参数类型列表
	 *
	 * @param methodParameterTypes 方法参数列表
	 * @return String 全类名参数类型列表
	 */
	static String[] getParameterTypes(Class<?>[] methodParameterTypes) {
		if (methodParameterTypes == null) {
			return null;
		}
		int length = methodParameterTypes.length;
		if (length == 0) {
			return new String[0];
		}
		String[] parameterTypes = new String[length];
		for (int index = 0; index < length; index++) {
			parameterTypes[index] = methodParameterTypes[index].getCanonicalName();
		}
		return parameterTypes;
	}

	/**
	 * 获取插件接口全类名
	 *
	 * @param annotation      插件注解
	 * @param beanTargetClass bean目标类型
	 * @return 插件接口全类名
	 */
	static String getInterfaceClassFullName(AddonProvider annotation, Class<?> beanTargetClass) {
		String interfaceFullName = annotation.interfaceName();
		if (StrUtil.isNotBlank(interfaceFullName)) {
			return interfaceFullName;
		}
		Class<?> interfaceClass = annotation.interfaceClass();
		if (interfaceClass != Exception.class) {
			return interfaceClass.getCanonicalName();
		}
		Class<?>[] interfaces = beanTargetClass.getInterfaces();
		if (ArrayUtil.isEmpty(interfaces)) {
			throw new RuntimeException(I18N.msg("addon.provider.interface.missed"));
		}
		return interfaces[0].getCanonicalName();
	}


}

```

# com.medusa.gruul.common.addon.provider.ISupportKeyGenerator

**文件路径**: `addon\provider\ISupportKeyGenerator.java`

## 代码文档
///
@author 张治保
date 2023/1/31
 
///

///
获取支持的support完整key

@param addonProvider 提供者注解
@return 完整key
     
///


## 文件结构
```
项目根目录
└── addon\provider
    └── ISupportKeyGenerator.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.provider;

/**
 * @author 张治保
 * date 2023/1/31
 */
@FunctionalInterface
public interface ISupportKeyGenerator {
    /**
     * 获取支持的support完整key
     *
     * @param addonProvider 提供者注解
     * @return 完整key
     */
    String key(AddonProvider addonProvider);
}

```

