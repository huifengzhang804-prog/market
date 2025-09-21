package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分销等级
 *
 * @author 张治保
 * date 2022/11/14
 */
@Getter
@RequiredArgsConstructor
public enum Level {

	/**
	 * 一级分销
	 */
	ONE(1),

	/**
	 * 二级分销
	 */
	TWO(2),

	/**
	 * 三级分销
	 */
	THREE(3);
	@EnumValue
	private final Integer value;


}
