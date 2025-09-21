package com.medusa.gruul.payment.service.mq;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.payment.service.service.ManagePaymentHistoryService;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 支付mq 监听
 *
 * @author xiaoq
 * @Description PaymentRabbitListener.java
 * @date 2022-09-29 18:06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRabbitListener {

	private final ManagePaymentHistoryService managePaymentHistoryService;


	/**
	 * 余额变化监控队列
	 *
	 * @param balanceChange 余额变化消息体
	 * @param channel       channel
	 * @param message       message
	 * @ throws IOException
	 */
	@Log("余额变化")
	@RabbitListener(queues = PaymentQueueNames.PAYMENT_CHANGE_CREATE_DETAIL_QUEUE)
	public void balanceChange(BalanceChangeDTO balanceChange, Channel channel, Message message) throws IOException {
		log.warn("receive message:".concat(balanceChange.toString()));
		managePaymentHistoryService.createBalanceDetail(balanceChange);
		//确认消息
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
}
