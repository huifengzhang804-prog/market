package com.medusa.gruul.carrier.pigeon.service.service;

import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;

import java.util.Map;

/**
 * @author WuDi
 * date: 2023/1/10
 */
public interface MessageAppletSubscribeService {

    /**
     * 发送小程序订阅消息
     * @param subscribeAppletMsg  小程序订阅消息
     */
    void sendAppletSubscribe(SubscribeAppletMsgDTO subscribeAppletMsg);

    /**
     * 获取小程序模板id
     *
     * @return 模板id
     */
    Map<SubscribeMsg, String> getTemplateIds();
}
