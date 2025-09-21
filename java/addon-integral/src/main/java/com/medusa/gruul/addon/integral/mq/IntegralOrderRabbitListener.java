package com.medusa.gruul.addon.integral.mq;

import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.dto.IntegralCompletionDTO;
import com.medusa.gruul.addon.integral.service.*;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 10:46
 **/

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegralOrderRabbitListener {

    private final CreateIntegralOrderService createIntegralOrderService;

    private final CloseIntegralOrderService closeIntegralOrderService;

    private final IntegralOrderPayService integralOrderPayService;
    private final ClientIntegralBehaviorService clientIntegralBehaviorService;
    private final IntegralOrderDeliverService integralOrderDeliverService;

    /**
     * 把订单数据刷新到数据库
     */
    @RabbitListener(queues = IntegralOrderQueueNames.INTEGRAL_ORDER_CREATE_FLUSH_DATA2DB_QUEUE)
    public void integralOrderCreate(IntegralOrderDetailInfoBO integralOrderDetailInfoBO, Channel channel, Message message) throws IOException {

        log.info("订单刷新到数据库的消息：{}", integralOrderDetailInfoBO);
        this.createIntegralOrderService.saveIntegralOrder2DbBatch(integralOrderDetailInfoBO);

        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 支付超时取消积分订单
     */
    @RabbitListener(queues = IntegralOrderQueueNames.INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE)
    public void integralOrderCancelNotPay(IntegralOrderDetailInfoBO integralOrderDetailInfoBO, Channel channel, Message message) throws IOException {

        log.info("支付超时取消积分订单消息：{}", integralOrderDetailInfoBO);
        this.closeIntegralOrderService.closeIntegralOrderPaidTimeout(integralOrderDetailInfoBO);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 积分订单支付结果
     */
    @RabbitListener(queues = IntegralOrderQueueNames.INTEGRAL_ORDER_PAID_CALLBACK_QUEUE)
    public void integralOrderPaidResult(PayNotifyResultDTO payNotifyResult, Channel channel, Message message) throws IOException {

        log.info("积分订单支付结果回调消息：{}", payNotifyResult);
        this.integralOrderPayService.payNotify(payNotifyResult);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 积分订单发货自动完成监听
     */
    @RabbitListener(queues = IntegralOrderQueueNames.INTEGRAL_ORDER_SEND_COMPLETION)
    public void integralOrderSendCompletion(IntegralCompletionDTO integralCompletion, Channel channel, Message message) throws IOException{
        integralOrderDeliverService.complete(true, integralCompletion.getIntegralOrderNo());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 订单完成监听
     *
     */
    @RabbitListener(queues = IntegralOrderQueueNames.INTEGRAL_USER_ORDER_FINISH_BEHAVIOR)
    public void userOrderFinishBehavior(OrderCompletedDTO orderCompletedDTO, Channel channel, Message message) throws IOException{
        clientIntegralBehaviorService.userOrderFinishBehavior(orderCompletedDTO);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
