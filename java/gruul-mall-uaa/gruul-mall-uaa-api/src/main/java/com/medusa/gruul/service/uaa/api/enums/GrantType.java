package com.medusa.gruul.service.uaa.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 认证类型枚举
 *
 * @author 张治保
 * date 2020/03/01
 */
@Getter
@RequiredArgsConstructor
public enum GrantType {

	/**
	 * 客户端密码认证
	 */
	CLIENT_CREDENTIALS("client_credentials", Boolean.FALSE),

	/**
	 * 隐式授权认证
	 */
	IMPLICIT("implicit", Boolean.FALSE),

	/**
	 * 授权码认证
	 */
	AUTHORIZATION_CODE("authorization_code", Boolean.FALSE),

	/**
	 * refreshToken刷新认证
	 */
	REFRESH_TOKEN("refresh_token", Boolean.FALSE),

	/**
	 * 用户名密码认证
	 */
	PASSWORD("password", Boolean.FALSE),

	/**
	 * 短信验证码认证
	 */
	SMS_CODE("sms_code", Boolean.FALSE),

	/**
	 * 微信授权
	 */
	WECHAT_CODE("wechat_code", Boolean.FALSE),

	/**
	 * 切换店铺
	 */
	SWITCH_SHOP("switch_shop", Boolean.TRUE);

	public static final String GRANT_TYPE_KEY = "grant_type";

	@EnumValue
	private final String value;
	/**
	 * 是否处于切换店铺的认证模式
	 */
	private final boolean switchShop;

	/**
	 * 根据value获取枚举
	 *
	 * @param value 值
	 * @return GrantType
	 */
	public static GrantType getEnum(String value) {
		if (value == null || value.length() == 0) {
			return null;
		}
		for (GrantType grantType : GrantType.values()) {
			if (grantType.getValue().equals(value)) {
				return grantType;
			}
		}
		return null;
	}
}
