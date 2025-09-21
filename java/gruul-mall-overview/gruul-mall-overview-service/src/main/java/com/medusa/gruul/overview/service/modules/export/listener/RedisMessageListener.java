package com.medusa.gruul.overview.service.modules.export.listener;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.overview.api.model.DownloadFileSseMessage;
import com.medusa.gruul.overview.service.modules.export.controller.DataExportRecordController;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 订阅监听器
 * @author jipeng
 * @since 2024/11/15
 */
@Component
public class RedisMessageListener implements MessageListener, ApplicationContextAware {
    ApplicationContext applicationContext;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 这里处理接收到的消息
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        System.out.println("Received message: " + body + " from channel: " + channel);
        DownloadFileSseMessage fileSseMessage = JSON.parseObject(body, DownloadFileSseMessage.class);
        DataExportRecordController controller = applicationContext.getBean(DataExportRecordController.class);
        ConcurrentHashMap<Long, List<SseEmitter>> clients = controller.getClients();
        List<SseEmitter> sseEmitters = clients.get(fileSseMessage.getFileId());
        if (sseEmitters!=null){
            for (SseEmitter sseEmitter : sseEmitters) {
                try {
                    sseEmitter.send(body);
                } catch (Exception e) {
                    sseEmitters.remove(sseEmitter);
                }
            }
            //一个下载文件只会推送一次 下载完成或者失败后都移除
            clients.remove(fileSseMessage.getFileId());
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
