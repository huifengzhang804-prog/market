package com.medusa.gruul.common.model.resp;

import com.medusa.gruul.global.model.exception.Error;

/**
 * 状态码接口
 *
 * @author L.cm
 */
public interface IResultCode extends Error {
	/**
	 * 返回的code码
	 *
	 * @return code
	 */
	int getCode();

	/**
	 * 返回的消息
	 *
	 * @return 消息
	 */
	String getMsg();

	/**
	 * 错误代码
	 *
	 * @return 错误代码
	 */
	@Override
	default int code() {
		return getCode();
	}

	/**
	 * 错误消息key
	 *
	 * @return 错误消息key
	 */
	@Override
	default String msg() {
		return getMsg();
	}
}
