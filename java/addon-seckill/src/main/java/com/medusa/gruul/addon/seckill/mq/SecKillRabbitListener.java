package com.medusa.gruul.addon.seckill.mq;

import com.medusa.gruul.addon.seckill.service.SeckillOrderService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author WuDi
 * date: 2022/12/5
 */
@Component
@RequiredArgsConstructor
public class SecKillRabbitListener {

    private final SeckillOrderService seckillOrderService;


    @Log("秒杀活动支付信息")
    @RabbitListener(queues = SeckillQueueNames.SECKILL_ORDER_PAID)
    public void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        seckillOrderService.orderPaid(orderPaidBroadcast);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
