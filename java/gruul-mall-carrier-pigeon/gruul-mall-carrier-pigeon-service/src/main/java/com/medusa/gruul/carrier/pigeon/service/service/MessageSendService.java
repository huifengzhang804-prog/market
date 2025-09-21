package com.medusa.gruul.carrier.pigeon.service.service;

import com.medusa.gruul.carrier.pigeon.api.model.StompTargetsMessage;

/**
 * @author 张治保
 * date 2022/5/5
 */
public interface MessageSendService {
    /**
     * 保存并发送消息
     * @param message 消息体
     */
    void saveAndPushMessage(StompTargetsMessage message);

    /**
     * 消息推送
     * @param message 消息详情
     * @param msgId 消息id
     */
    void pushMessage(Long msgId, StompTargetsMessage message);
    /**
     * 保存消息
     * @param message 消息详情
     * @return 消息id
     */
    Long saveMessage(StompTargetsMessage message);
}
