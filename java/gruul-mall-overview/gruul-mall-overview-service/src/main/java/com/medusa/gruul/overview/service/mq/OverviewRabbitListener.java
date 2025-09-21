package com.medusa.gruul.overview.service.mq;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.model.StatementCreateDTO;
import com.medusa.gruul.overview.api.model.WithdrawOrderDTO;
import com.medusa.gruul.overview.service.modules.base.service.OverviewBaseService;
import com.medusa.gruul.overview.service.modules.operate.service.ManageDealRankingService;
import com.medusa.gruul.overview.service.modules.statement.service.StatementSaveService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import com.medusa.gruul.payment.api.model.param.SharingResult;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author WuDi
 * date 2022/10/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverviewRabbitListener {

	private final WithdrawService withdrawService;
	private final OverviewBaseService overviewBaseService;
	private final StatementSaveService statementSaveService;
	private final ManageDealRankingService manageDealRankingService;

	@Log("分账状态同步")
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_SHARING_STATUS_SYNC)
	public void sharingStatusSync(SharingResult result, Channel channel, Message message) throws IOException {
		statementSaveService.sharingStatusSync(result);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	@Log("对账单")
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_STATEMENT)
	public void statementSave(OverviewStatementDTO overviewStatement, Channel channel, Message message) throws IOException {
		statementSaveService.saveStatement(message.getMessageProperties().getMessageId(), overviewStatement);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	@Log("创建对账单，一般用于插件发送消息")
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_STATEMENT_CREATE)
	public void statementCreate(StatementCreateDTO statementCreate, Channel channel, Message message) throws IOException {
		statementSaveService.createStatement(message.getMessageProperties().getMessageId(), statementCreate);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	@Log("订单售后关闭")
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_ORDER_AFS_CLOSE_QUEUE)
	public void orderAfsClosed(OrderInfo orderClosed, Channel channel, Message message) throws IOException {
		statementSaveService.orderAfsClosed(message.getMessageProperties().getMessageId(), orderClosed);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}


	/**
	 * 订单评价完成
	 */
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_ORDER_COMMENTED)
	@Log("订单已完成")
	public void dealRankingChange(OrderCompletedDTO orderCompleted, Channel channel, Message message) throws IOException {
		//对账单
		statementSaveService.orderAccomplish(orderCompleted, message.getMessageProperties().getMessageId());
		//店铺销售额
		List<ShopOrderItem> shopOrderItems = orderCompleted.getShopOrderItems();
		if (CollUtil.isNotEmpty(shopOrderItems)) {
			manageDealRankingService.dealRankingChange(shopOrderItems);
		}
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	/**
	 * 订单发货生成运费对账单信息
	 */
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_ORDER_DELIVER)
	@Log("订单已发货,生成运费信息")
	public void statementFreightSave(OrderPackageKeyDTO orderPackageKey, Channel channel, Message message) throws IOException {
		statementSaveService.statementFreightSave(orderPackageKey, message.getMessageProperties().getMessageId());
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}


	/**
	 * 生成提现工单
	 */
	@Log("生成提现工单")
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_WITHDRAW_CREATE_QUEUE)
	public void createWithdrawOrder(WithdrawOrderDTO withdrawOrder, Channel channel, Message message) throws IOException {
		log.error("withdrawOrder");
		withdrawService.createOrder(message.getMessageProperties().getMessageId(), withdrawOrder);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	/**
	 * 店铺信息更改
	 */
	@RabbitListener(queues = OverviewQueueNames.OVERVIEW_SHOP_UPDATE)
	public void shopUpdate(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
		overviewBaseService.updateShopInfo(shopInfo);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}


}
