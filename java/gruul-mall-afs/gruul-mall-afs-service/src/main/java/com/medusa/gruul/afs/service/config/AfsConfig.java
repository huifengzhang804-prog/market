package com.medusa.gruul.afs.service.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.afs.service.properties.AfsConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: WuDi
 * @date: 2022/10/18
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AfsConfig {

    /**
     * 异步执行线程池
     */
    @Bean
    public Executor afsExecutor(AfsConfigurationProperties afsConfigurationProperties) {
        AfsConfigurationProperties.TaskThreadPool taskThreadPool = afsConfigurationProperties.getThreadPool();
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
