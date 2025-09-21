package com.medusa.gruul.addon.platform.properties;

import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author 张治保
 * @since 2024/2/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.addon.platform")
public class PlatformConfigurationProperties {

    /**
     * 装修页面缓存过期时间 单位秒 默认7天
     */
    Duration pageCacheExpire = Duration.ofDays(CommonPool.NUMBER_SEVEN);

}
