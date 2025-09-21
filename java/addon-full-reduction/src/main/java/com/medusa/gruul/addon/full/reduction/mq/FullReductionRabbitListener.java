package com.medusa.gruul.addon.full.reduction.mq;

import com.medusa.gruul.addon.full.reduction.components.FullReductionOrderExists;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionOrderStatus;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionOrderDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReductionOrder;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullReductionRabbitListener {


    private final FullReductionOrderDao fullReductionOrderDao;
    private final FullReductionOrderExists fullReductionOrderExists;

    /**
     * 添加满减活动支付信息
     */
    @Log("订单支付-满减检查")
    @RabbitListener(queues = FullReductionRabbitQueueNames.FULL_REDUCTION_ORDER_PAID_QUEUE)
    public void fullReductionPaymentInfo(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        String orderNo = orderPaidBroadcast.getOrderNo();
        //如果没有产生满减订单则直接返回
        if (!fullReductionOrderExists.exists(orderNo)) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        fullReductionOrderDao.lambdaUpdate()
                .eq(FullReductionOrder::getOrderNo, orderNo)
                .set(FullReductionOrder::getStatus, FullReductionOrderStatus.PAID)
                .eq(FullReductionOrder::getStatus, FullReductionOrderStatus.UNPAID)
                .update();
        fullReductionOrderExists.remove(orderNo);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
