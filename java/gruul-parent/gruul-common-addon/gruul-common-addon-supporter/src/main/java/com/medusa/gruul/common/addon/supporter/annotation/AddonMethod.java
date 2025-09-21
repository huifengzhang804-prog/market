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
