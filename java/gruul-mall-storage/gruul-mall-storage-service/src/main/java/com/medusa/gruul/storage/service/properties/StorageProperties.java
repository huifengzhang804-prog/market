package com.medusa.gruul.storage.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * date 2022/7/6
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.storage")
public class StorageProperties {

    /**
     * sku缓存信息过期时间 单位:秒
     */
    private long skuExpireTime = 2 * 24 * 60 * 60;

}
