package com.medusa.gruul.common.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.crypt")
public class CryptProperties {
    /**
     * 是否开启加解密
     */
    private boolean enable = true;

    /**
     * 参数解密私钥
     */
    private String privateKey;


    /**
     * 参数加密公钥
     */
    private String publicKey;

}
