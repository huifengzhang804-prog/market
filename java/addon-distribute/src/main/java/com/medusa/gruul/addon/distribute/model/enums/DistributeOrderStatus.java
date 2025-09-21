package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分销的订单状态
 *
 * @author 张治保
 * date 2022/11/17
 */
@Getter
@RequiredArgsConstructor
public enum DistributeOrderStatus {

	/**
	 * 待付款
	 */
	UNPAID(1),

	/**
	 * 已付款
	 */
	PAID(2),

	/**
	 * 已完成
	 */
	COMPLETED(3),

	/**
	 * 已关闭
	 */
	CLOSED(4);
	@EnumValue
	private final Integer value;
}
