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
