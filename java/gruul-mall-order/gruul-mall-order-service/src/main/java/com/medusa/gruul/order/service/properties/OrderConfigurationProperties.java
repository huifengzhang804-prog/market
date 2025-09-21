package com.medusa.gruul.order.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author 张治保
 * date 2022/6/10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.order")
public class OrderConfigurationProperties {

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
         * 创建订单缓存时间 单位秒
         */
        private long createOrderCache = 20 * 60;
        /**
         * sku 库存 限购 缓存时间 默认两天 单位秒
         */
        private long stockLimit = 2 * 24 * 60 * 60;

        /**
         * 小程序获取 运力id 缓存时间 1天
         */
        private long miniAppDeliveryCache = 24 * 60 * 60;
    }


}
