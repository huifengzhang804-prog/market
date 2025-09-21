package com.medusa.gruul.shop.service.mq;

import com.medusa.gruul.shop.service.service.ShopManageService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 店铺MQ监听
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShopRabbitListener {

    private final ShopManageService shopManageService;

    /**
     * 监听店铺上架商品数量变化
     *
     * @param shopId
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = ShopQueueNames.SHOP_GOODS_COUNT_CHANGE_QUEUE)
    public void shopOnShelfGoodsCountChangeListener(Long shopId,
                                                    Channel channel, Message message) throws IOException {
        shopManageService.updateShopOnShelfGoodsCount(shopId);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
