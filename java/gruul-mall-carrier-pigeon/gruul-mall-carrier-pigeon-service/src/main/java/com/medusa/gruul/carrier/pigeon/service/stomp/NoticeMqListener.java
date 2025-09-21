package com.medusa.gruul.carrier.pigeon.service.stomp;

import com.medusa.gruul.carrier.pigeon.api.PigeonConst;
import com.medusa.gruul.carrier.pigeon.api.model.StompTargetsMessage;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 提醒广播mq监听器
 * @author 张治保
 * date 2022/4/29
 */
@Component
@RequiredArgsConstructor
public class NoticeMqListener {

    private final MessageSendService noticeSendService;

    @RabbitListener(queuesToDeclare = @Queue(name = PigeonConst.PIGEON_NOTICE_SEND_QUEUE))
    public void sendMessage(StompTargetsMessage message){
        noticeSendService.saveAndPushMessage(message);
    }
}
