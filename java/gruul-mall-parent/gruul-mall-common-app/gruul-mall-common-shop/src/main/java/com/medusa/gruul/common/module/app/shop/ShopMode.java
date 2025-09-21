package com.medusa.gruul.common.module.app.shop;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 店铺运营模式
 *
 * @author 张治保
 * date 2023/7/13
 */
@Getter
@RequiredArgsConstructor
public enum ShopMode {

	/**
	 * 普通店铺
	 */
	COMMON(1),

	/**
	 * 供应商
	 */
	SUPPLIER(2),

	/**
	 * O2O店铺
	 */
	O2O(3),

	;
	@EnumValue
	private final Integer value;
}
