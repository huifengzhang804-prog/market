package com.medusa.gruul.addon.integral.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.addon.integral.properties.IntegralOrderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张治保
 * date 2023/9/4
 */
@Configuration
@RequiredArgsConstructor
public class IntegralConfig {
    private final IntegralOrderProperties integralOrderProperties;

    @Bean
    public TaskExecutor integralTaskExecutor() {
        IntegralOrderProperties.TaskThreadPool taskThreadPool = integralOrderProperties.getThreadPool();
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
    public Executor integralExecutor() {
        return TtlExecutors.getTtlExecutor(integralTaskExecutor());
    }
}
