package com.medusa.gruul.carrier.pigeon.service.mq;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.carrier.pigeon.api.sms.SmsSendParam;
import com.medusa.gruul.carrier.pigeon.service.im.service.CarrierUserService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsSignService;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageAppletSubscribeService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author 张治保
 * date 2022/10/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PigeonRabbitListener {

    private final MessageSender messageSender;
    private final SmsSignService smsSignService;
    private final IMessageShopService messageShopService;
    private final MessageAppletSubscribeService messageAppletSubscribeService;

    private final CarrierUserService userService;

    /**
     * 发送提醒消息mq队列
     */
    @RabbitListener(queues = PigeonRabbitQueueNames.PIGEON_ORDER_PAID_BROADCAST_QUEUE)
    public void sendMessage(OrderPaidBroadcastDTO orderPaidBroadcast, Channel channel, Message message) throws IOException {
        List<OrderPaidBroadcastDTO.ShopPayAmountDTO> shopPayAmounts = orderPaidBroadcast.getShopPayAmounts();
        CollUtil.emptyIfNull(shopPayAmounts).parallelStream().forEach(
                shopPayAmount -> messageSender.send(
                        SendType.MARKED_SHOP.getDestination(shopPayAmount.getShopId()),
                        () -> com.medusa.gruul.carrier.pigeon.api.enums.Channel.NEW_ORDER
                )
        );
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 店铺信息变更
     */
    @RabbitListener(queues = PigeonRabbitQueueNames.PIGEON_SHOP_INFO_UPDATE_QUEUE)
    public void shopInfoUpdate(ShopInfoVO shopInfo, Channel channel, Message message) throws IOException {
        if (shopInfo != null && shopInfo.getId() != null) {
            TenantShop.disable(() -> messageShopService.lambdaUpdate()
                    .set(MessageShop::getShopName, shopInfo.getName())
                    .set(MessageShop::getShopLogo, shopInfo.getLogo())
                    .eq(MessageShop::getShopId, shopInfo.getId())
                    .update());
        }
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 短信发送
     *
     * @param msg     短信发送dto
     * @param message properties
     * @param channel channel
     */
    @RabbitListener(queues = PigeonRabbitQueueNames.PIGEON_SEND_SMS)
    public void smsSendMessage(SmsSendParam msg, Message message, Channel channel) throws IOException {
        log.debug("sms_send message:" + message.toString());
        if (msg != null) {
            smsSignService.smsSend(msg);
        }
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 发送小程序订阅消息
     */
    @RabbitListener(queues = PigeonRabbitQueueNames.PIGEON_APPLET_SUBSCRIBE_QUEUE)
    public void sendAppletSubscribe(SubscribeAppletMsgDTO subscribeAppletMsg, Channel channel, Message message) throws IOException {
        messageAppletSubscribeService.sendAppletSubscribe(subscribeAppletMsg);
        // 确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 修改redis个人资料
     */
    @RabbitListener(queues = PigeonRabbitQueueNames.USER_DATA_UPDATE)
    public void updateUserData(UserBaseDataDTO userData, Channel channel, Message message) throws IOException {
        userService.updateUserInfo(userData);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
