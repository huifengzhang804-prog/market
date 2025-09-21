package com.medusa.gruul.overview.service.mq;

import com.medusa.gruul.common.model.enums.StatementRabbit;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author WuDi
 * date 2022/10/10
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OverviewRabbitConfig {


	/**
	 * overview mq
	 */
	@Bean
	@ConditionalOnMissingBean(name = "overviewExchange")
	public Exchange overviewExchange() {
		return ExchangeBuilder.directExchange(StatementRabbit.EXCHANGE)
				.durable(true)
				.delayed().build();
	}

	/**
	 * 订单 exchange
	 */
	@Bean
	@ConditionalOnMissingBean(name = "orderExchange")
	public Exchange orderExchange() {
		return ExchangeBuilder.directExchange(OrderRabbit.EXCHANGE)
				.durable(true)
				.delayed()
				.build();
	}

	/**
	 * shop mq
	 */
	@Bean
	@ConditionalOnMissingBean(name = "shopExchange")
	public Exchange shopExchange() {
		return ExchangeBuilder.directExchange(ShopRabbit.EXCHANGE)
				.durable(true)
				.build();
	}




	/**
	 * 对账单
	 * todo 未来将舍弃
	 *
	 * @deprecated
	 */
	@Bean
	@ConditionalOnMissingBean(name = "statementExchange")
	public Exchange statementExchange() {
		return ExchangeBuilder.directExchange(StatementRabbit.EXCHANGE)
				.durable(true)
				.delayed().build();
	}

	/**
	 * todo 未来将舍弃
	 *
	 * @deprecated
	 */
	@Bean
	public Queue overviewStatementQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_STATEMENT)
				.build();
	}

	/**
	 * todo 未来将舍弃
	 *
	 * @deprecated
	 */
	@Bean
	public Binding overviewStatementBinding() {
		return BindingBuilder.bind(overviewStatementQueue())
				.to(overviewExchange())
				.with(StatementRabbit.OVERVIEW_STATEMENT.routingKey())
				.noargs();
	}

	/**
	 * 分账状态同步
	 */
	@Bean
	public Queue overviewSharingStatusSyncQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_SHARING_STATUS_SYNC)
				.build();
	}

	@Bean
	public Binding overviewSharingStatusSyncBinding() {
		return BindingBuilder.bind(overviewSharingStatusSyncQueue())
				.to(overviewExchange())
				.with(OverviewRabbit.SHARING_STATUS_SYNC.routingKey())
				.noargs();
	}

	/**
	 * 创建对账单
	 */
	@Bean
	public Queue overviewStatementCreateQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_STATEMENT_CREATE)
				.build();
	}

	@Bean
	public Binding overviewStatementCreateBinding() {
		return BindingBuilder.bind(overviewStatementCreateQueue())
				.to(overviewExchange())
				.with(OverviewRabbit.STATEMENT_CREATE.routingKey())
				.noargs();
	}

	/**
	 * 订单售后关闭监听
	 */
	@Bean
	public Queue overviewOrderAfsCloseQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_ORDER_AFS_CLOSE_QUEUE)
				.build();
	}

	@Bean
	public Binding overviewOrderAfsBinding() {
		return BindingBuilder.bind(overviewOrderAfsCloseQueue())
				.to(orderExchange())
				.with(OrderRabbit.ORDER_CLOSE.routingKey())
				.noargs();

	}

	/**
	 * 订单发货监听
	 */

	@Bean
	public Queue overviewOrderDeliverQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_ORDER_DELIVER).build();
	}

	@Bean
	public Binding overviewOrderDeliverBinding() {
		return BindingBuilder.bind(overviewOrderDeliverQueue())
				.to(orderExchange())
				.with(OrderRabbit.ORDER_DELIVER_GOODS.routingKey())
				.noargs();
	}

	/**
	 * 订单已评价监听
	 */
	@Bean
	public Queue overviewOrderCommentedQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_ORDER_COMMENTED)
				.build();
	}

	@Bean
	public Binding overviewOrderCommentedBinding() {
		return BindingBuilder.bind(overviewOrderCommentedQueue())
				.to(orderExchange())
				.with(OrderRabbit.ORDER_ACCOMPLISH.routingKey())
				.noargs();
	}

	/**
	 * 提现工单监听队列
	 */
	@Bean
	public Queue withdrawCreateQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_WITHDRAW_CREATE_QUEUE).build();
	}

	@Bean
	public Binding withdrawCreateBinding() {
		return BindingBuilder.bind(withdrawCreateQueue())
				.to(overviewExchange())
				.with(OverviewRabbit.WITHDRAW_ORDER_CREATE.routingKey())
				.noargs();
	}

	/**
	 * 店铺信息更改
	 */
	@Bean
	public Queue shopUpdateQueue() {
		return QueueBuilder.durable(OverviewQueueNames.OVERVIEW_SHOP_UPDATE).build();
	}

	@Bean
	public Binding shopUpdateBinding() {
		return BindingBuilder.bind(shopUpdateQueue())
				.to(shopExchange())
				.with(ShopRabbit.SHOP_UPDATE.routingKey())
				.noargs();
	}

}
