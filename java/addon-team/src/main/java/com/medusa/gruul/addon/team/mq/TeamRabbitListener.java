package com.medusa.gruul.addon.team.mq;

import com.medusa.gruul.addon.team.service.TeamRabbitService;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 拼团处理订单mq
 *
 * @author 张治保
 * date 2023/3/16
 */
@Component
@RequiredArgsConstructor
public class TeamRabbitListener {

    private final TeamRabbitService teamRabbitService;

    /**
     * 订单支付成功
     *
     * @param orderPaid 订单支付成功消息
     * @param channel   通道
     * @param message   消息
     * @throws IOException io异常
     */
    @RabbitListener(queues = TeamRabbitNames.ORDER_PAID)
    public void orderPaid(OrderPaidBroadcastDTO orderPaid, Channel channel, Message message) throws IOException {
        teamRabbitService.orderPaid(orderPaid);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    

}
