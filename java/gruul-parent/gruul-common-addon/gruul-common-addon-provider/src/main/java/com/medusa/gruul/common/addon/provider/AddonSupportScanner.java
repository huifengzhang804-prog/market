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
