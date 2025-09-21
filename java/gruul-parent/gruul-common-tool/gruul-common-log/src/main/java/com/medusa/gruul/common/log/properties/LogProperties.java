package com.medusa.gruul.common.log.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志配置类
 * @author 张治保
 * date 2022/2/15
 */
@ConfigurationProperties(prefix = "logging")
@Getter
@Setter
public class LogProperties {

    private Set<String> headerNames = new HashSet<>();

}
