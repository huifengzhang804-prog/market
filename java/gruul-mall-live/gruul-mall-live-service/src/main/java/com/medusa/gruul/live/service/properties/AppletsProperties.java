package com.medusa.gruul.live.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author miskw
 * @date 2022/11/8
 */
@ConfigurationProperties(prefix = "wechat.applets")
@Component
@Getter
@Setter
public class AppletsProperties {
    private String appid;
    private String secret;
}
