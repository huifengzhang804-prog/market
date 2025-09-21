package com.medusa.gruul.addon.platform.mq;

import com.medusa.gruul.addon.platform.service.SigningCategoryService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.shop.api.enums.OperaReason;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PlatformRabbitListener
 *
 * @author xiaoq
 * @Description PlatformRabbitListener.java
 * @date 2023-09-06 15:31
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformRabbitListener {

    private final SigningCategoryService signingCategoryService;

    /**
     * 店铺状态改变 禁用/开启
     *
     * @param shopsEnableDisable 店铺开启禁用DTO
     * @param channel            Channel
     * @param message            Message
     */
    @Log("店铺状态改变")
    @RabbitListener(queues = PlatformQueueNames.SHOP_CHANGE_QUEUE)
    public void shopChange(ShopsEnableDisableDTO shopsEnableDisable, Channel channel, Message message) throws IOException {
        log.error("receive message:{}", shopsEnableDisable);
        shopsEnableDisable.validParam();
        if (shopsEnableDisable.getReason() == OperaReason.DELETED) {
            // 店铺删除 同时删除对应签约类目
            signingCategoryService.clearShopSigningCategory(shopsEnableDisable.getShopIds());
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
