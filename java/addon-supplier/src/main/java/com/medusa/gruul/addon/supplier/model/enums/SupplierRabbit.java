package com.medusa.gruul.addon.supplier.model.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/7/22
 */
@Getter
@RequiredArgsConstructor
public enum SupplierRabbit implements RabbitParent {

	/**
	 * 供应商订单支付超时自动关闭
	 */
	SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE("paid.timeout.close"),
	;
	public static final String EXCHANGE = "supplier.direct";

	private final String routingKey;

	@Override
	public String exchange() {
		return EXCHANGE;
	}

	@Override
	public String routingKey() {
		return routingKey;
	}
}
