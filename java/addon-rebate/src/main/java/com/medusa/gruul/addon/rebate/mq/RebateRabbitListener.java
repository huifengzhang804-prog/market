package com.medusa.gruul.addon.rebate.mq;

import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author jinbu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RebateRabbitListener {

    private final RebateOrderHandlerService rebateOrderHandlerService;


    /**
     * 订单已创建
     */
    @Log("订单已创建")
    @RabbitListener(queues = RebateRabbitQueueNames.REBATE_ORDER_CREATED)
    public void rebateOrderCreate(OrderCreatedDTO orderCreated, Channel channel, Message message) throws IOException {
        log.debug("订单已创建: orderCreated:{}", orderCreated);
        rebateOrderHandlerService.saveRebateOrderCreated(orderCreated);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单已支付
     */
    @RabbitListener(queues = RebateRabbitQueueNames.REBATE_ORDER_PAYED)
    public void rebateOrderPayed(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        log.debug("订单已支付: orderPaidBroadcast: {}", orderPaidBroadcast);
        rebateOrderHandlerService.rebateOrderPayed(orderPaidBroadcast);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单关闭
     */
    @RabbitListener(queues = RebateRabbitQueueNames.REBATE_ORDER_CLOSED)
    public void rebateOrderClosed(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        log.debug("订单关闭: orderInfo: {}", orderInfo);
        if (orderInfo.getBuyerId() != null) {
            rebateOrderHandlerService.rebateOrderClosed(orderInfo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单完成
     */
    @RabbitListener(queues = RebateRabbitQueueNames.REBATE_ORDER_ACCOMPLISH)
    public void rebateOrderAccomplish(OrderCompletedDTO orderCompleted, Channel channel, Message message) throws IOException {
        log.debug("订单完成: orderCompleted: {}", orderCompleted);
        rebateOrderHandlerService.rebateOrderAccomplish(orderCompleted);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 提现工单被关闭
     */
    @RabbitListener(queues = RebateRabbitQueueNames.WITHDRAW_ORDER_FORBIDDEN)
    public void rebateWithdrawOrderForbidden(OverviewWithdraw overviewWithdraw, Channel channel, Message message) throws IOException {
        log.debug("提现工单被关闭: overviewWithdraw: {}", overviewWithdraw);
        rebateOrderHandlerService.rebateWithdrawOrderForbidden(overviewWithdraw);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
