package com.medusa.gruul.addon.bargain.mq;

import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainPaymentInfoService;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wudi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BargainRabbitListener {

    private final IBargainPaymentInfoService bargainPaymentInfoService;
    private final IBargainOrderService bargainOrderService;


    /**
     * 订单已支付
     */
    @RabbitListener(queues = BargainQueueNames.BARGAIN_ORDER_PAID)
    public void bargainPaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        if (orderPaidBroadcast.getActivityType() == OrderType.BARGAIN) {
            log.debug("砍价活动支付信息: {}", orderPaidBroadcast);
            bargainPaymentInfoService.bargainPaymentInfo(orderPaidBroadcast);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 砍价订单创建失败
     */
    @RabbitListener(queues = BargainQueueNames.BARGAIN_ORDER_CREATE_FAIL)
    public void bargainOrderCreateFail(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        if (OrderType.BARGAIN == orderInfo.getActivityType()) {
            log.debug("砍价订单创建失败: {}", orderInfo);
            bargainOrderService.bargainOrderCreateFail(orderInfo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 砍价活动支付退款
     */
    @RabbitListener(queues = BargainQueueNames.BARGAIN_REFUND_INFO)
    public void bargainRefundInfo(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        if (orderInfo.getActivityType() == OrderType.BARGAIN) {
            log.debug("砍价活动支付退款: {}", orderInfo);
            bargainPaymentInfoService.bargainRefundInfo(orderInfo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 发起砍价队列
     */
    @RabbitListener(queues = BargainQueueNames.BARGAIN_START)
    public void updateBargainStatus(BargainOrder bargainOrder, Channel channel, Message message) throws IOException {
        log.debug("更新砍价状态: {}", bargainOrder);
        bargainOrderService.updateBargainStatus(bargainOrder);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
