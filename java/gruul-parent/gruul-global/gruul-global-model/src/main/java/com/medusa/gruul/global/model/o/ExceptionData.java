package com.medusa.gruul.global.model.o;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionData implements Serializable {

	/**
	 * 引发错误的 key 如 店铺 id 或店铺 id 与商品 id 组合行程的 key
	 */
	private Serializable key;

	/**
	 * 引发错误的状态数据  比如店铺状态异常 可以传递店铺状态
	 */
	private Serializable data;

	public static ExceptionData of(Serializable key) {
		return ExceptionData.of(key, null);
	}

	public static ExceptionData of(Serializable key, Serializable data) {
		return new ExceptionData(key, data);
	}

}
