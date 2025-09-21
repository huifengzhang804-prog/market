package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 配送状态
 *
 * @author 张治保
 * date 2022/3/9
 */
@Getter
@RequiredArgsConstructor
public enum PackageStatus {
	/**
	 * 待发货 0
	 */
	WAITING_FOR_DELIVER(0, false, true, false),
	/**
	 * 待收货 1
	 */
	WAITING_FOR_RECEIVE(1, false, true, false),
	/**
	 * 买家确认收货 待评价 2
	 */
	BUYER_WAITING_FOR_COMMENT(WAITING_FOR_RECEIVE.value + 1, false, true, true),
	/**
	 * 系统确认收货 待评价 3
	 */
	SYSTEM_WAITING_FOR_COMMENT(BUYER_WAITING_FOR_COMMENT.value + 1, false, true, true),
	/**
	 * 买家已评论 已完成 4
	 */
	BUYER_COMMENTED_COMPLETED(SYSTEM_WAITING_FOR_COMMENT.value + 1, true, false, false),
	/**
	 * 系统自动好评 已完成 5
	 */
	SYSTEM_COMMENTED_COMPLETED(BUYER_COMMENTED_COMPLETED.value + 1, true, false, false);

	@EnumValue
	private final Integer value;
	/**
	 * 是否已完成
	 */
	private final boolean completed;
	/**
	 * 是否可以售后
	 */
	private final boolean canAfs;
	/**
	 * 是否可以评价
	 */
	private final boolean canComment;
}
