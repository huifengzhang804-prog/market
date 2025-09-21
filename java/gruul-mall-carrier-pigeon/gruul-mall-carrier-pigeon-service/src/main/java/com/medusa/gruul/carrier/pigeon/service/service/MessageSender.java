package com.medusa.gruul.carrier.pigeon.service.service;

import com.medusa.gruul.carrier.pigeon.api.model.BaseMessage;

/**
 * 消息发送器
 *
 * @author 张治保
 * date 2022/10/12
 */
public interface MessageSender {

    /**
     * 发送消息
     *
     * @param destination 发送目标 topic
     * @param message     消息体
     */
    void send(String destination, BaseMessage message);
}
