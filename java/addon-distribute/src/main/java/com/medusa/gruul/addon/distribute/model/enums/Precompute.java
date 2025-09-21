package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 预计算 展示
 *
 * @author 张治保
 * date 2023/5/11
 */
@Getter
@RequiredArgsConstructor
public enum Precompute {

	/**
	 * 对所有人展示
	 */
	ALL(1),

	/**
	 * 从不展示
	 */
	NEVER(2),

	/**
	 * 仅对分销员展示
	 */
	DISTRIBUTOR(3);
	@EnumValue
	private final Integer value;
}
