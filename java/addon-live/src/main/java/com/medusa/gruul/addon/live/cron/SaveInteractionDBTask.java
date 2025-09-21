package com.medusa.gruul.addon.live.cron;

import com.medusa.gruul.addon.live.service.LiveUserService;
import com.medusa.gruul.addon.live.socket.config.NettySocketConfig;
import io.netty.channel.group.ChannelGroup;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe 存储数据
 */
@Slf4j
@Component
public class SaveInteractionDBTask {

    @Resource
    private LiveUserService userService;

    /**
     * 存储点赞、观看人数到数据库
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void interactionDB() {
        Map<String, ChannelGroup> allClientMap = NettySocketConfig.ALL_CLIENT_MAP;
        //直播间互动数据落库
        userService.saveInteractionDbBatch(allClientMap.keySet());

    }
}
