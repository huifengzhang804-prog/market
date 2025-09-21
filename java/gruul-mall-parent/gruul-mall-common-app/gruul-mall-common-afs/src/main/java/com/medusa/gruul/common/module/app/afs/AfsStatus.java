package com.medusa.gruul.common.module.app.afs;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 售后状态
 * </p>
 *
 * @author 张治保
 * date 2022/8/3
 */
@Getter
@RequiredArgsConstructor
public enum AfsStatus {
	/**
	 * 不处于售后状态
	 */
	NONE(
			0,
			false,
			false,
			false,
			true,
			false,
			true,
			"未知","不属于售后状态"
	),
	/**
	 * 退款申请
	 */
	REFUND_REQUEST(
			10,
			true,
			false,
			false,
			false,
			true,
			true,
			"待处理","待商家审核"
	),

	/**
	 * 退款已同意
	 */
	REFUND_AGREE(
			20,
			false,
			true,
			false,
			false,
			false,
			false,
			"退款中","退款已同意"
	),

	/**
	 * 系统自动同意退款申请
	 */
	SYSTEM_REFUND_AGREE(
			21,
			false,
			true,
			false,
			false,
			false,
			false,
			"退款中","系统自动同意退款申请"
	),

	/**
	 * 退款已拒绝
	 */
	REFUND_REJECT(
			30,
			false,
			false,
			false,
			true,
			true,
			true,
			"退款失败","退款申请已拒绝"
	),

	/**
	 * 已退款
	 */
	REFUNDED(
			40,
			false,
			false,
			true,
			false,
			false,
			false,
			"退款成功","申请通过已退款成功"
	),

	/**
	 * 退货退款申请
	 */
	RETURN_REFUND_REQUEST(
			50,
			true,
			false,
			false,
			false,
			true,
			true,
			"待处理","待商家审核"
	),

	/**
	 * 退货退款已同意
	 */
	RETURN_REFUND_AGREE(
			60,
			false,
			false,
			false,
			false,
			true,
			true,
			"待退货","已同意退货待买家退货"
	),

	/**
	 * 系统自动同意退货退款申请
	 */
	SYSTEM_RETURN_REFUND_AGREE(
			61,
			false,
			false,
			false,
			false,
			true,
			true,
			"待退货","系统自动通过待买家退货"
	),

	/**
	 * 退货退款已拒绝
	 */
	RETURN_REFUND_REJECT(
			70,
			false,
			false,
			false,
			true,
			true,
			true,
			"退款失败","退款申请已拒绝"
	),

	/**
	 * 退货退款 买家已发货
	 */
	RETURNED_REFUND(
			80,
			false,
			false,
			false,
			false,
			false,
			false,
			"退货中","买家已发货"
	),

	/**
	 * 退货退款 卖家已确认收货
	 */
	RETURNED_REFUND_CONFIRM(
			90,
			false,
			true,
			false,
			false,
			false,
			false,
			"待退款","商家已确认收货"
	),

	/**
	 * 退货退款 系统自动确认收货
	 */
	SYSTEM_RETURNED_REFUND_CONFIRM(
			91,
			false,
			true,
			false,
			false,
			false,
			false,
			"待退款","系统自动确认收货"
	),

	/**
	 * 退货退款 卖家拒绝收货退回 售后关闭
	 */
	RETURNED_REFUND_REJECT(
			100,
			false,
			false,
			false,
			true,
			true,
			true,
			"退款失败","商家拒绝收货"
	),

	/**
	 * 已退货退款 已完成
	 */
	RETURNED_REFUNDED(
			110,
			false,
			false,
			true,
			false,
			false,
			false,
			"退款成功","已退货退款成功"
	),

	/**
	 * 买家撤销申请
	 */
	BUYER_CLOSED(
			120,
			false,
			false,
			false,
			true,
			false,
			true,
			"退款失败","买家撤销退款申请"
	),

	/**
	 * 系统自动关闭
	 */
	SYSTEM_CLOSED(
			130,
			false,
			false,
			false,
			true,
			false,
			true,
			"退款失败","系统自动关闭"
	);

	@EnumValue
	private final Integer value;

	/**
	 * 是否处于 可以同意/决绝申请的状态
	 */
	private final boolean canAgreeOrReject;

	/**
	 * 是否处于可以退款状态
	 */
	private final boolean canRefunded;

	/**
	 * 是否已处于退款状态
	 */
	private final boolean refunded;

	/**
	 * 是否可以再次申请
	 */
	private final boolean canReRequest;

	/**
	 * 是否可以关闭
	 */
	private final boolean canClose;

	/**
	 * 是否可以继续更改商品状态
	 */
	private final boolean canChangePackageStatus;
	/**
	 * 售后状态
	 */
	private final String statusStr;
	/**
	 * 状态描述
	 */
	private final String statusDesc;
}
