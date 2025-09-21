package com.medusa.gruul.addon.matching.treasure.mq;

import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealPaymentInfoService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealProductService;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 搭配宝队列
 */
@Component
@RequiredArgsConstructor
public class MatchingTreasureRabbitListener {

    private final ISetMealProductService setMealProductService;
    private final ISetMealPaymentInfoService setMealPaymentInfoService;


    /**
     * 套餐活动支付信息
     */
    @RabbitListener(queues = MatchingTreasureQueueNames.MATCHING_TREASURE_PAYMENT_INFO)
    public void matchingTreasurePaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        if (orderPaidBroadcast.getActivityType() == OrderType.PACKAGE) {
            setMealPaymentInfoService.matchingTreasurePaymentInfo(orderPaidBroadcast);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    /**
     * 套餐活动支付退款
     */
    @RabbitListener(queues = MatchingTreasureQueueNames.MATCHING_TREASURE_REFUND_INFO)
    public void matchingTreasureRefundInfo(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        if (orderInfo.getActivityType() == OrderType.PACKAGE) {
            setMealPaymentInfoService.matchingTreasureRefundInfo(orderInfo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    /**
     * 商品更新
     */
    @RabbitListener(queues = MatchingTreasureQueueNames.PRODUCT_UPDATE)
    public void productUpdate(ProductBroadcastDTO productUpdate, Channel channel, Message message) throws IOException {
        setMealProductService.productUpdate(productUpdate);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
