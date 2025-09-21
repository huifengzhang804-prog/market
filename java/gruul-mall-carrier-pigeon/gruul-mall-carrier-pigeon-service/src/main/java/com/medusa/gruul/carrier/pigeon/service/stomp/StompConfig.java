package com.medusa.gruul.carrier.pigeon.service.stomp;

import com.medusa.gruul.carrier.pigeon.service.properties.RabbitStompProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author 张治保
 * date 2022/05/05
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitStompProperties rabbitStompProperties;
    private final AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //连接前缀
        registry.addEndpoint("/pigeon/stomp")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /*
         * 客户端发送消息 需要带前缀 /app
         * toUser /queue/
         */
        config.setApplicationDestinationPrefixes("/app/")
                .setUserDestinationPrefix("/queue/");
        /*
         * 配置RabbitMQ代理
         * exchange 自定义交换机 交换机需要主动创建
         * topic 群发
         * queue 点对点
         * 使用默认的 交换机
         * 目前只用topic 实现广播/群发/私聊
         */
        config.enableStompBrokerRelay("/topic/")
                .setRelayHost(rabbitStompProperties.getHost())
                .setRelayPort(rabbitStompProperties.getPort())
                .setClientLogin(rabbitStompProperties.getUsername())
                .setClientPasscode(rabbitStompProperties.getPassword())
                .setSystemLogin(rabbitStompProperties.getUsername())
                .setSystemPasscode(rabbitStompProperties.getPassword())
                .setVirtualHost(rabbitStompProperties.getVirtualHost());

    }

    /**
     * 配置认证拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor);
    }
}