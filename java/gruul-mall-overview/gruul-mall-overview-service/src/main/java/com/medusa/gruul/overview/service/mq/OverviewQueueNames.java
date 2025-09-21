package com.medusa.gruul.overview.service.mq;

/**
 * 消息队列名称
 *
 * @author WuDi
 * date 2022/10/18
 */
public interface OverviewQueueNames {

	/**
	 * 对账单
	 */
	String OVERVIEW_STATEMENT = "overview.statement";

	/**
	 * 对账单
	 */
	String OVERVIEW_STATEMENT_CREATE = "overview.statement.create";

	/**
	 * 订单一支付
	 */
	String OVERVIEW_ORDER_PAID = "overview.order.paid";

	/**
	 * 订单售后关闭完成
	 */
	String OVERVIEW_ORDER_AFS_CLOSE_QUEUE = "overview.order.afs.close";

	/**
	 * 订单已评价
	 */
	String OVERVIEW_ORDER_COMMENTED = "overview.order.commented";

	/**
	 * 新增提现工单mq
	 */
	String OVERVIEW_WITHDRAW_CREATE_QUEUE = "overview.withdraw.create";

	/**
	 * 店铺信息更改
	 */
	String OVERVIEW_SHOP_UPDATE = "overview.shop.update";


	/**
	 * 订单发货
	 */
	String OVERVIEW_ORDER_DELIVER = "overview.order.deliver";

	/**
	 * 分账状态同步
	 */
	String OVERVIEW_SHARING_STATUS_SYNC = "overview.sharing.status.sync";
}
