package com.medusa.gruul.live.service.mq;

import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.live.service.service.LiveBroadcastService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author miskw
 * @date 2022/11/18
 * 直播mq监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LiveRabbitListener {
    private final LiveBroadcastService liveBroadcastService;

    /**
     * 平台下架商品 关联直播商品违规下架
     *
     * @param dtoList 商品修改状态dto
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = LiveRabbitQueueNames.LIVE_CHANGE_QUEUE)
    public void liveChangeQueue(List<ProductUpdateStatusDTO> dtoList, Channel channel, Message message) throws IOException {
        log.debug("----------平台下架违规直播商品--------");
        liveBroadcastService.liveChangeQueue(dtoList);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
