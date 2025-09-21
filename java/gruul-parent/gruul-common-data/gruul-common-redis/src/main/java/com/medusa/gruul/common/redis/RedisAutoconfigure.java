package com.medusa.gruul.common.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.redis.aspect.RedissonAdvisor;
import com.medusa.gruul.common.redis.aspect.RedissonInterceptor;
import com.medusa.gruul.common.redis.properties.GruulRedisProperties;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.config.GlobalAppProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.time.Duration;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/2/16
 */
@RequiredArgsConstructor
@Import({RedissonAdvisor.class, RedissonInterceptor.class})
@EnableConfigurationProperties(GruulRedisProperties.class)
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate", "org.springframework.data.redis.connection.RedisConnectionFactory"})
public class RedisAutoconfigure {


    private final GruulRedisProperties redisProperties;

    /**
     * 配置RedisListener
     */
    @Bean
    @ConditionalOnProperty(prefix = "gruul.redis", name = "enable-message-listener", havingValue = "true")
    @ConditionalOnClass(name = "org.springframework.data.redis.listener.RedisMessageListenerContainer")
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Primary
    @Bean("gruulRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory,
            RedissonClient redissonClient,
            GlobalAppProperties globalAppProperties
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(RedisUtil.LazyRedisSerializer.KEY_SERIALIZER);
        template.setHashKeySerializer(RedisUtil.LazyRedisSerializer.HASH_KEY_SERIALIZER);

        template.setValueSerializer(RedisUtil.valueSerializer());
        template.setHashValueSerializer(RedisUtil.valueSerializer());
        //初始化RedisUtil
        RedisUtil.init(
                template,
                redissonClient,
                globalAppProperties.getName(),
                redisProperties.getDoubleDeletionScheduleCorPoolSize()
        );
        return template;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            GlobalAppProperties globalAppProperties
    ) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(
                        (cacheName) -> RedisUtil.key(
                                StrUtil.replace(globalAppProperties.getName(), StrPool.DASHED, StrPool.COLON),
                                cacheName
                        ) + StrPool.COLON
                );
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(
                        CollUtil.emptyIfNull(redisProperties.getExpires())
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                GruulRedisProperties.Expire::getKey,
                                                expire -> config.entryTtl(
                                                        Duration.ofMillis(
                                                                expire.getUnit().toMillis(expire.getTtl())
                                                        )
                                                )
                                        )
                                )
                ).build();
    }
}
