package com.medusa.gruul.addon.matching.treasure.config;


import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.addon.matching.treasure.properties.MatchingTreasureConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@RequiredArgsConstructor
public class MatchingTreasureConfig {


    private final MatchingTreasureConfigurationProperties matchingTreasureConfigurationProperties;


    @Bean
    public Executor matchingTreasureTaskExecutor() {
        MatchingTreasureConfigurationProperties.TaskThreadPool taskThreadPool = matchingTreasureConfigurationProperties.getThreadPool();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(taskThreadPool.getThreadNamePrefix());
        executor.setCorePoolSize(taskThreadPool.getCorePoolSize());
        executor.setMaxPoolSize(taskThreadPool.getMaxPoolSize());
        executor.setQueueCapacity(taskThreadPool.getQueueCapacity());
        executor.setKeepAliveSeconds(taskThreadPool.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }

}
