package com.medusa.gruul.overview.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * 概况mq配置
 *
 * @author WuDi
 * date: 2022/10/10
 */
@RequiredArgsConstructor
public enum OverviewRabbit implements RabbitParent {

	/**
	 * 创建对账单
	 */
	STATEMENT_CREATE("overview.statement.create"),

	/**
	 * 创建提现工单
	 */
	WITHDRAW_ORDER_CREATE("overview.withdraw.create"),

	/**
	 * 提现工单申请通过
	 */
	WITHDRAW_ORDER_SUCCESS("overview.withdraw.success"),

	/**
	 * 提现工单已禁用
	 */
	WITHDRAW_ORDER_FORBIDDEN("overview.withdraw.forbidden"),

	/**
	 * 分账状态同步
	 */
	SHARING_STATUS_SYNC("overview.sharing.sync"),

	;


	public static final String EXCHANGE = "overview.direct";
	/**
	 * 路由key
	 */
	private final String routingKey;

	@Override
	public String exchange() {
		return OverviewRabbit.EXCHANGE;
	}

	@Override
	public String routingKey() {
		return routingKey;
	}
}
