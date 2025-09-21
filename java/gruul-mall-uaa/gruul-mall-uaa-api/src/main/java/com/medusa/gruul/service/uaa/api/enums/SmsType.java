package com.medusa.gruul.service.uaa.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短信类型
 *
 * @author 张治保
 * date 2022/10/25
 */
@Getter
@RequiredArgsConstructor
public enum SmsType {

	/**
	 * 登陆
	 */
	LOGIN("gruul:mall:uaa:login:captcha:sms"),

	/**
	 * 重置密码
	 */
	RESET_PASSWORD("gruul:mall:uaa:reset:password:captcha:sms"),

	/**
	 * 鉴权
	 */
	AUTHENTICATION("gruul:mall:uaa:login:captcha:sms"),

	/**
	 * 店铺门店创建
	 */
	SHOP_STORE_FOUND("gruul:mall:uaa:shop:store:found:captcha:sms"),

	/**
	 * 分销商操作
	 */
	DISTRIBUTOR("gruul:mall:uaa:distributor:captcha:sms");


	private final String key;

}
