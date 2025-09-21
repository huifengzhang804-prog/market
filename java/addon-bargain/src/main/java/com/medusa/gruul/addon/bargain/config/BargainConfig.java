package com.medusa.gruul.addon.bargain.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.addon.bargain.properties.BargainConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wudi
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BargainConfig {


    private final BargainConfigurationProperties bargainConfigProperties;


    @Bean
    public TaskExecutor bargainTaskExecutor() {
        BargainConfigurationProperties.TaskThreadPool taskThreadPool = bargainConfigProperties.getThreadPool();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(taskThreadPool.getThreadNamePrefix());
        executor.setCorePoolSize(taskThreadPool.getCorePoolSize());
        executor.setMaxPoolSize(taskThreadPool.getMaxPoolSize());
        executor.setQueueCapacity(taskThreadPool.getQueueCapacity());
        executor.setKeepAliveSeconds(taskThreadPool.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 异步执行线程池
     */
    @Bean
    public Executor bargainExecutor() {
        return TtlExecutors.getTtlExecutor(bargainTaskExecutor());
    }
}
