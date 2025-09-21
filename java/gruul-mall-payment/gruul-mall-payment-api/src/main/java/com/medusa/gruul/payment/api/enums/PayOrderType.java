package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/7
 */
@Getter
@RequiredArgsConstructor
public enum PayOrderType {

	/**
	 * 普通订单
	 */
	COMMON(1),

	/**
	 * 合单订单
	 */
	COMBINE(2);

	@EnumValue
	private final Integer value;
}
