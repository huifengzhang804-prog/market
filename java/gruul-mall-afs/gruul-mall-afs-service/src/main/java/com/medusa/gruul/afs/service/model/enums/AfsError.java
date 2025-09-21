package com.medusa.gruul.afs.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/25
 */
@Getter
@RequiredArgsConstructor
public enum AfsError implements Error {

	/**
	 * 该订单项已关闭
	 */
	ITEM_CLOSED(70000, "afs.item.closed"),

	/**
	 * 该订单项处于不可申请售后状态
	 */
	ITEM_CANNOT_AFS_STATUS(70001, "afs.item.cannot.afs.status"),

	/**
	 * 该比订单不存在
	 */
	ITEM_NOT_EXIST(70002, "afs.item.not.exist"),

	/**
	 * 下一个售后状态不能为空
	 */
	AFS_NEXT_STATUS_NOT_NULL(70003, "afs.next.status.not.null"),

	/**
	 * 不支持的发货方式
	 */
	NOT_SUPPORT_DELIVER_TYPE(70004, "afs.not.support.deliver.type"),

	/**
	 * 物流公司不能为空
	 */
	EXPRESS_COMPANY_NOT_NULL(70005, "afs.express.company.not.null"),

	/**
	 * 无法审批当前售后状态
	 */
	NOT_SUPPORT_APPROVAL_STATUS(70006, "afs.not.support.approval.status"),

	/**
	 * 未设置默认退货地址
	 */
	NOT_DEFAULT_RETURN_ADDRESS(70007, "afs.not.default.return.address"),

	/**
	 * 当前不处于可退货状态
	 */
	NOT_SUPPORT_RETURN_STATUS(70008, "afs.not.support.return.status"),

	/**
	 * 不处于可拒绝退货的售后状态
	 */
	NOT_SUPPORT_REFUSE_RETURN_STATUS(70009, "afs.not.support.refuse.return.status"),

	/**
	 * 不处于可关闭的售后状态
	 */
	NOT_SUPPORT_CLOSE_STATUS(70010, "afs.not.support.close.status"),

	/**
	 * 售后类型和售后原因不匹配
	 */
	TYPE_REASON_NOT_MATCH(70011, "afs.type.reason.not.match"),

	/**
	 * 未发货售后类型不正确
	 */
	NOT_DELIVERED_TYPE_ERROR(70012, "afs.not.delivered.type.error"),

	/**
	 * 申请的退款金额不能大于订单实际支付金额
	 */
	REFUND_AMOUNT_CANNOT_GREATER_THAN_PAID_AMOUNT(70013, "afs.refund.amount.cannot.greater.than.paid.amount"),

	/**
	 * 售后工单状态和订单状态不匹配
	 */
	AFS_STATUS_NOT_MATCH_ORDER_STATUS(70014, "afs.status.not.match.order.status"),
	/**
	 * 售后订单上传失败
	 */
	AFS_ORDER_UPLOADER_ERROR(70015, "afs.order.uploader.error"),
	/**
	 * 临时售后工单生成失败
	 *
	 */
	TEMP_AFS_ORDER_GENERATE_ERROR(70016, "afs.temp.afs.order.generate.error"),
	;

	private final int code;
	private final String msgCode;

	@Override
	public int code() {
		return getCode();
	}

	@Override
	public String msg() {
		return I18N.msg(getMsgCode());
	}
}
