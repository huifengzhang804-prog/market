package com.medusa.gruul.overview.service.modules.statement.service;

import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.model.StatementCreateDTO;
import com.medusa.gruul.payment.api.model.param.SharingResult;

/**
 * 保存对账单
 *
 * @author: WuDi
 * @date: 2022/10/18
 */
public interface StatementSaveService {

	/**
	 * 保存对账单
	 *
	 * @param msgId             消息id 用于幂等性校验
	 * @param overviewStatement 对账单DTO
	 */
	void saveStatement(String msgId, OverviewStatementDTO overviewStatement);


	/**
	 * 创建对账单 一版给插件使用
	 *
	 * @param msgId           消息id 用于幂等性校验
	 * @param statementCreate 对账单必要信息
	 */
	void createStatement(String msgId, StatementCreateDTO statementCreate);

	/**
	 * 订单评价完成
	 *
	 * @param orderCompleted 订单完成数据
	 * @param msgId          mq消息id 用于幂等性校验
	 */
	void orderAccomplish(OrderCompletedDTO orderCompleted, String msgId);

	/**
	 * 订单发货
	 *
	 * @param orderPackageKey 订单发货数据
	 * @param messageId       mq消息id 用于幂等性校验
	 */
	void statementFreightSave(OrderPackageKeyDTO orderPackageKey, String messageId);

	/**
	 * 订单售后关闭
	 *
	 * @param msgId       消息id 用于分账账单/幂等性校验
	 * @param orderClosed 订单关闭数据
	 */
	void orderAfsClosed(String msgId, OrderInfo orderClosed);

	/**
	 * 分账状态同步
	 *
	 * @param result 分账结果
	 */
	void sharingStatusSync(SharingResult result);
}
