package com.medusa.gruul.shop.service.properties;

import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 * @author 张治保
 * date 2022/6/10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.shop")
public class ShopConfigurationProperties {

    /**
     * 装修页面缓存过期时间 单位秒 默认7天
     */
    private Duration pageCacheExpire = Duration.ofDays(CommonPool.NUMBER_SEVEN);

    /**
     * 缓存时间配置
     */
    @NestedConfigurationProperty
    private CacheExpire cacheExpire = new CacheExpire();

    /**
     * 缓存 过期时间 单位秒
     */
    @Getter
    @Setter
    public static class CacheExpire {
        /**
         * 店铺信息缓存时间 默认 3天时间
         */
        private long shopExpireTime = 3 * 24 * 60 * 60;

        /**
         * 续期 生效时间段 24小时内自动续期
         */
        private long shopRenewalTime = 24 * 60 * 60;
    }

}
