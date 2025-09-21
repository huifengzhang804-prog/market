package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum ChatWithType {

	/**
	 * 店铺
	 */
	SHOP(1),

	/**
	 * 平台
	 */
	PLAT_FORM(2),

	/**
	 * C端用户
	 */
	CONSUMER(3);


	@EnumValue
	private final Integer value;
}
