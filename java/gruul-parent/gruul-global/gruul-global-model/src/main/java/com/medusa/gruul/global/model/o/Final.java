package com.medusa.gruul.global.model.o;

import java.io.Serializable;

/**
 * 用于在 函数式编程里使用 局部变量 需要注意的是 不是线程安全的
 * (函数编程里要求 final 变量 如果使用AtomicReference有点太重了)
 *
 * @author 张治保
 * date 2023/4/13
 */
public final class Final<T> implements Serializable {

	/**
	 * 数据
	 */
	private T data;

	public Final() {
		this(null);
	}

	public Final(T data) {
		this.data = data;
	}

	/**
	 * 获取数据
	 *
	 * @return T
	 */
	public T get() {
		return data;
	}

	/**
	 * 设置数据
	 *
	 * @param data 数据
	 */
	public void set(T data) {
		this.data = data;
	}

}
