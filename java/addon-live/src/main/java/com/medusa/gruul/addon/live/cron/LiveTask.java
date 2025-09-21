package com.medusa.gruul.addon.live.cron;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.addon.live.socket.config.NettySocketConfig;
import com.medusa.gruul.addon.live.constant.LiveConstants;
import com.medusa.gruul.addon.live.socket.dto.InteractionSocketDTO;
import com.medusa.gruul.addon.live.socket.dto.SocketMessageDTO;
import com.medusa.gruul.addon.live.socket.enums.SocketMessageType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @author miskw
 * @date 2023/6/16
 * @describe 描述
 */
@Slf4j
@Component
public class LiveTask {
    /**
     * 每分钟推送通信
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void liveTask() {
        Map<String, ChannelGroup> allClientMap = NettySocketConfig.ALL_CLIENT_MAP;
        if (MapUtil.isEmpty(allClientMap)) {
            return;
        }
        allClientMap.forEach((liveId, channelGroups) -> {
            //发送点赞数信息
            sendLikeCountMessage(liveId, channelGroups);
            //发送观看人数信息
            sendViewershipCountMessage(liveId, channelGroups);
        });
    }

    /**
     * 观看人数
     * @param liveId 直播间id
     * @param channelGroups 对应直播间所有连接
     */
    private static void sendViewershipCountMessage(String liveId, ChannelGroup channelGroups) {
        //传输观看数
        Integer viewership = (Integer) Option.of(RedisUtil.getCacheObject(RedisUtil.key(LiveConstants.LIVE_VIEWERSHIP, liveId)))
                .getOrElse(0);
        InteractionSocketDTO viewershipDTO=new InteractionSocketDTO()
                .setCount(viewership)
                .setLiveId(liveId);
        SocketMessageDTO viewershipMessage = new SocketMessageDTO()
                .setType(SocketMessageType.VIEWERSHIP)
                .setData(viewershipDTO);
        channelGroups.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(viewershipMessage)));
    }

    /**
     *
     * @param liveId 直播间id
     * @param channelGroups 对应直播间所有连接
     */
    private static void sendLikeCountMessage(String liveId, ChannelGroup channelGroups) {
        SocketMessageDTO likeMessage = new SocketMessageDTO()
                .setType(SocketMessageType.LIKE);
        Integer likeCount = (Integer) Option.of(RedisUtil.getCacheObject(RedisUtil.key(LiveConstants.LIVE_LIKE, liveId)))
                .getOrElse(0);
        InteractionSocketDTO likeSocketDTO=new InteractionSocketDTO()
                .setCount(likeCount)
                .setLiveId(liveId);
        likeMessage.setData(likeSocketDTO);
        //传输点赞数
        channelGroups.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(likeMessage)));
    }
}
