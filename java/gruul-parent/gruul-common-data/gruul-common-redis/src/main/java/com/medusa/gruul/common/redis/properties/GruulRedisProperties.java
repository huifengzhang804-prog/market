package com.medusa.gruul.common.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * date 2022/3/19
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.redis")
public class GruulRedisProperties {

    /**
     * RedisMessageListenerContainer
     */
    private Boolean enableMessageListener = Boolean.FALSE;

    /**
     * 过期时间配置
     */
    private List<Expire> expires = new ArrayList<>();

    /**
     * 缓存延迟双删的核心线程数 默认为8
     */
    private int doubleDeletionScheduleCorPoolSize = 8;

    @Getter
    @Setter
    public static class Expire {
        /**
         * 缓存key
         */
        private String key;
        /**
         * 缓存时长
         */
        private long ttl;
        /**
         * 缓存单位
         */
        private TimeUnit unit = TimeUnit.SECONDS;
    }
}
