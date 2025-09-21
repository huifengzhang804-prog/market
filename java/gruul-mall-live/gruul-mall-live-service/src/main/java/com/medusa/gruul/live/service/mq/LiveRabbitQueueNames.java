package com.medusa.gruul.live.service.mq;

/**
 * @author miskw
 * @date 2022/12/5
 */
public interface LiveRabbitQueueNames {
    /**
     * 平台下架商品，直播商品违规下架队列
     */
    String LIVE_CHANGE_QUEUE= "goods.live.change.queue";
}
