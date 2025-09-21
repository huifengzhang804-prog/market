package com.medusa.gruul.addon.coupon.mq;

import com.medusa.gruul.addon.coupon.service.CouponOnOrderCloseService;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 张治保
 * date 2022/11/11
 */
@Component
@RequiredArgsConstructor
public class CouponRabbitListener {

    private final CouponOnOrderCloseService couponOnOrderCloseService;


    @RabbitListener(queues = CouponRabbitQueueName.COUPON_ORDER_CLOSED)
    public void orderCreateFailQueue(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        couponOnOrderCloseService.orderClose(orderInfo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
