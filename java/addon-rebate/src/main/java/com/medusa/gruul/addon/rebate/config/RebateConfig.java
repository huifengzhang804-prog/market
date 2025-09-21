package com.medusa.gruul.addon.rebate.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.addon.rebate.properties.RebateConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class RebateConfig {

    private final RebateConfigurationProperties rebateConfigurationProperties;


    @Bean
    public TaskExecutor rebateTaskExecutor() {
        RebateConfigurationProperties.TaskThreadPool taskThreadPool = rebateConfigurationProperties.getThreadPool();
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
    public Executor rebateOrderExecutor() {
        return TtlExecutors.getTtlExecutor(rebateTaskExecutor());
    }
}
