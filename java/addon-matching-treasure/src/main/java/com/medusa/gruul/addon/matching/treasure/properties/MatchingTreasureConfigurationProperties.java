package com.medusa.gruul.addon.matching.treasure.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.addon.matching.treasure")
public class MatchingTreasureConfigurationProperties {

    /**
     * 缓存时间配置
     */
    private CacheExpire cacheExpire = new CacheExpire();
    /**
     * 线程池配置
     */
    private TaskThreadPool threadPool = new TaskThreadPool();

    /**
     * 缓存 过期时间 单位秒
     */
    @Getter
    @Setter
    public static class CacheExpire {

        /**
         * 缓存时间 /秒
         */
        private long coupon = 2 * 60 * 60;

        /**
         * 缓存时间
         */
        private long userCoupon = 60 * 60;
    }

    /**
     * 线程池配置详情
     */
    @Getter
    @Setter
    public static class TaskThreadPool {

        /**
         * 线程池线程名前缀
         */
        private String threadNamePrefix = "MatchingTreasure-Future";
        /**
         * 核心线程数
         */
        private int corePoolSize = 10;
        /**
         * 最大线程数
         */
        private int maxPoolSize = 25;
        /**
         * 线程存活时间长度
         */
        private int keepAliveSeconds = 60;
        /**
         * 任务队列长度
         */
        private int queueCapacity = 800;
    }
}
