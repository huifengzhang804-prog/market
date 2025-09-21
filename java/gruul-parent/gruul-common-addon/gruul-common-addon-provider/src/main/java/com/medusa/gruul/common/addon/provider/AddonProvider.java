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
