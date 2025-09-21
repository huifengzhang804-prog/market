package com.medusa.gruul.carrier.pigeon.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.medusa.gruul.carrier.pigeon.api.model.BaseMessage;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class MessageSenderImpl implements MessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;


    @Override
    public void send(String destination, BaseMessage message) {
        if (Objects.isNull(destination)) {
            return;
        }
        simpMessagingTemplate.convertAndSend(destination, JSON.toJSONString(message, JSONWriter.Feature.WriteLongAsString));
    }
}
