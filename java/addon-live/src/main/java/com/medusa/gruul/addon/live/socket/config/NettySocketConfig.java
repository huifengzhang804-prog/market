package com.medusa.gruul.addon.live.socket.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.addon.live.socket.dto.SocketMessageDTO;
import com.medusa.gruul.addon.live.socket.function.LiveSocket;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.netty.annotation.*;
import com.medusa.gruul.common.netty.domain.NettySession;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.common.web.util.SpringUtils;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 开源地址
 * <a href="https://gitee.com/Yeauty/netty-websocket-spring-boot-starter?_from=gitee_search">...</a>
 */
@Slf4j
@Component
@WebSocketEndpoint(
        path = "/ws/live",
        port = {8223})
public class NettySocketConfig {
    public static Map<String, ChannelGroup> ALL_CLIENT_MAP = new ConcurrentHashMap<>();

    @BeforeHandshake
    public void handshake(NettySession session) {
        session.setSubprotocols("stomp");
    }

    @OnOpen
    public void onOpen(NettySession session, @RequestParam MultiValueMap<String, String> reqMap) {
        log.warn("-------------新的客户端连接-----------");
        log.info("请求参数:{}", JSONUtil.toJsonStr(reqMap));
        if (MapUtil.isEmpty(reqMap)) {
            log.error("socket连接参数为空");
            session.sendText(new TextWebSocketFrame("socket连接参数为空"));
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "socket连接参数为空");
        }
        String liveId = reqMap.get("liveId").get(0);
        ChannelGroup channelsNew = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelsNew.add(session.channel());

        ChannelGroup channels = ALL_CLIENT_MAP.putIfAbsent(liveId, channelsNew);
        if (channels != null) {
            channels.add(session.channel());
        }
        log.warn("--------------" + ALL_CLIENT_MAP.size() + "-------------");
    }

    @OnClose
    public void onClose(NettySession session) throws IOException {
        if (session != null) {
            session.close();
        }
        log.info("----------关闭了一个连接-------");
    }

    @OnError
    public void onError(NettySession session, Throwable throwable) {
        throw new GlobalException(SystemCode.REQ_REJECT_CODE, "netty连接异常,原因:" + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(NettySession session, String message) {
        //接收前端消息
        if (StrUtil.isEmpty(message)) {
            return;
        }
        log.info("接收到一条消息，消息内容:{}", message);
        SocketMessageDTO socketMessageDTO = JSONUtil.toBean(message, SocketMessageDTO.class);
        Handler<Boolean> handler = SpringUtils.getBean(LiveSocket.class, socketMessageDTO.getType());
        handler.handle(socketMessageDTO.getData());
    }

}