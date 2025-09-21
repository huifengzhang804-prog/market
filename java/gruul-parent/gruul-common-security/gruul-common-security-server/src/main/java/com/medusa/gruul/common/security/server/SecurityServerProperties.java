package com.medusa.gruul.common.security.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * security server 配置
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.server")
public final class SecurityServerProperties {

    /**
     * 是否允许重复登录 默认不允许 同一种客户端
     */
    private boolean repeatLogin = false;

    /**
     * jwt 私钥
     */
    private String privateKey;

    /**
     * token 过期时间
     */
    private Duration tokenExpired = Duration.ofHours(2);

    /**
     * refresh token 过期时间
     */
    private Duration refreshTokenExpired = Duration.ofDays(7);
}
