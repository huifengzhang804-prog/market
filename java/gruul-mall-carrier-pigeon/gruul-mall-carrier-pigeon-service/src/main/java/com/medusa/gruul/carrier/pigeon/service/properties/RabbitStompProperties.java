package com.medusa.gruul.carrier.pigeon.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * date 2022/5/6
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.rabbitmq.stomp")
public class RabbitStompProperties {
    /**
     * rabbitmq 服务器host
     */
    private String host;
    /**
     * stomp监听端口
     */
    private int port = 61613;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 虚拟服务器
     */
    private String virtualHost;
}
