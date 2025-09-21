package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 佣金分摊类型
 *
 * @author 张治保
 * date 2022/11/14
 */
@Getter
@RequiredArgsConstructor
public enum ShareType {

	/**
	 * 付款比率
	 */
	RATE(1),

	/**
	 * 固定金额
	 */
	FIXED_AMOUNT(2);

	@EnumValue
	private final Integer value;

}
