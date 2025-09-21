package com.medusa.gruul.addon.live.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author miskw
 * @date 2023/6/3
 * @describe 描述
 */
@Data
@ConfigurationProperties(prefix = "gruul.tencent")
public class AddressKeyProperties {
    /**
     * 推流域名
     */
    private String pushAuthentication;
    /**
     * 推流地址key
     */
    private String pushAuthenticationKey;
    /**
     *
     */
    private String pullAuthentication;
    /**
     * 拉流地址key
     */
    private String pullAuthenticationKey;
    /**
     * 腾讯云secretId
     */
    private String secretId;
    /**
     * 腾讯云SecretKey
     */
    private String secretKey;
    /**
     * 腾讯云IM SDKAppId
     */
    private Long imAppId;
    /**
     * 密钥信息
     */
    private String imKey;

}
