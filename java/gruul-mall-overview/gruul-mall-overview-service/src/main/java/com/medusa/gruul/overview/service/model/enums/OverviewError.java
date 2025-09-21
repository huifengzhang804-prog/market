package com.medusa.gruul.overview.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * overview 异常码
 *
 * @author 张治保
 * date 2023/6/28
 */
@Getter
@RequiredArgsConstructor
public enum OverviewError implements Error {
	/**
	 * 暂不支持的提现方式
	 */
	NOT_SUPPORT_DRAW_TYPE(90000, "overview.error.not.support.draw.type"),

	/**
	 * 余额不足
	 */
	INSUFFICIENT_BALANCE(90001, "overview.error.insufficient.balance"),

	/**
	 * 店铺不存在
	 */
	SHOP_NOT_EXIST(90002, "overview.error.shop.not.exist"),

	/**
	 * 店铺收款信息不存在
	 */
	SHOP_DRAW_NOT_EXIST(90003, "overview.error.shop.draw.not.exist"),

	/**
	 * 采购订单已经存在
	 */
	PURCHASE_ORDER_ALREADY_EXIST(90004, "overview.error.shop.purchase.order.already.exist"),

	/**
	 * 采购订单不存在
	 */
	PURCHASE_ORDER_NOT_EXIST(90005, "overview.error.shop.purchase.order.not.exist"),
	/**
	 * 数据导出记录不存在
	 */
	DATA_EXPORT_RECORD_NOT_EXIST(90006, "overview.error.data.export.record.not.exist"),
	/**
	 * 数据导出记录不能更新
	 */
	DATA_EXPORT_RECORD_CAN_NOT_UPDATE(90007, "overview.error.data.export.can.not.update"),

	DATA_EXPORT_RECORD_STATUS_ERROR(90008, "overview.error.data.export.record.status.error"),

	DATA_EXPORT_RECORD_DOWN_LOAD_ERROR(90008, "overview.error.data.export.record.down.load.error"),
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
