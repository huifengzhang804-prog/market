package com.medusa.gruul.addon.live.socket.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.live.socket.config.NettySocketConfig;
import com.medusa.gruul.addon.live.socket.service.ISocketService;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author miskw
 * @date 2023/6/21
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class SocketServiceImpl implements ISocketService {
    /**
     * 关闭这个直播间socket通道
     * @param liveIds 直播间ids
     */
    @Override
    public void closeSocketChannel(List<String> liveIds) {
        if (CollUtil.isEmpty(liveIds)){
            return;
        }
        Map<String, ChannelGroup> allClientMap = NettySocketConfig.ALL_CLIENT_MAP;
        for (String liveId : liveIds) {
            if (allClientMap.containsKey(liveId)) {
                ChannelGroup channels = allClientMap.get(liveId);
                if (channels != null) {
                    channels.close();
                }
                allClientMap.remove(liveId);
            }
        }

    }
}
