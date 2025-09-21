package com.medusa.gruul.user.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: WuDi
 * @date: 2022/10/17
 */
@Data
@ConfigurationProperties(prefix = "gruul.user")
public class UserConfigurationProperties {
}
