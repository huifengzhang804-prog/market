package com.medusa.gruul.afs.service.mq;

import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.service.model.dto.AfsAuditDTO;
import com.medusa.gruul.afs.service.model.dto.AfsMqMessageDTO;
import com.medusa.gruul.afs.service.service.AfsService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * 售后mq监听
 * </p>
 *
 * @author 张治保
 * date 2022/8/8
 */

@Component
@RequiredArgsConstructor
public class AfsRabbitListener {

    private final AfsService afsService;

    /**
     * 自动同意售后申请
     */
    @RabbitListener(queues = AfsQueueNames.AFS_AUTO_AGREE_REQUEST_QUEUE)
    public void afsAutoAgreeAfsRequest(AfsMqMessageDTO afsMqMessage, Channel channel, Message message) throws IOException {
        afsService.afsRequestAgreeReject(
                afsMqMessage.getAfsNo(),
                new AfsAuditDTO()
                        .setAgree(Boolean.TRUE)
                        .setCurrentStatus(afsMqMessage.getCurrentStatus()),
                Boolean.TRUE
        );
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 自动确认商家收到退货
     */
    @RabbitListener(queues = AfsQueueNames.AFS_AUTO_CONFIRM_RETURNED_QUEUE)
    public void afsAutoConfirmReturnedRefund(AfsMqMessageDTO afsMqMessage, Channel channel, Message message) throws IOException {
        afsService.afsReturnedConfirmReject(
                afsMqMessage.getAfsNo(),
                new AfsAuditDTO()
                        .setAgree(Boolean.TRUE)
                        .setCurrentStatus(afsMqMessage.getCurrentStatus()),
                Boolean.TRUE

        );
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 自动关闭售后工单
     */
    @RabbitListener(queues = AfsQueueNames.AFS_AUTO_CLOSE_QUEUE)
    public void afsAutoClose(AfsMqMessageDTO afsMqMessage, Channel channel, Message message) throws IOException {
        afsService.afsClose(
                new AfsCloseDTO()
                        .setAfsNo(afsMqMessage.getAfsNo())
                        .setIsSystem(Boolean.TRUE)
                        .setUpdateShopOrderItem(Boolean.TRUE)
                        .setCurrentStatus(afsMqMessage.getCurrentStatus())
                        .setReason("买家超时未处理，系统自动关闭")

        );
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 售后退款成功回调
     */
    @Log("售后退款成功回调")
    @RabbitListener(queues = AfsQueueNames.AFS_REFUNDED_QUEUE)
    public void afsRefunded(RefundNotifyResultDTO result, Channel channel, Message message) throws IOException {
        afsService.refundedNotify(result.getAfsNum(), result.getRefundNum());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
