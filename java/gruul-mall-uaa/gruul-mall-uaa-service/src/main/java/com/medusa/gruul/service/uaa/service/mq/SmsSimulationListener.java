package com.medusa.gruul.service.uaa.service.mq;

import com.medusa.gruul.service.uaa.service.service.impl.SmsServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 短信模拟、真实的监听器
 * @author jipeng
 * @since 2025/1/2
 */
@Component
@Slf4j
@AllArgsConstructor
public class SmsSimulationListener implements ChannelAwareMessageListener {

    private final ApplicationContext applicationContext;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());

        log.info("收到消息：{}", msg);
        SmsServiceImpl bean = applicationContext.getBean(SmsServiceImpl.class);
        bean.setSmsSimulation(Boolean.valueOf(msg));

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
