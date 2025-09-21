package com.medusa.gruul.global.config.helper;

import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池辅助类
 *
 * @author 张治保
 * @since 2023/12/8
 */
public interface ExecutorHelper {

    /**
     * 线程池通用配置详情
     *
     * @param executorProperties 线程池通用配置详情
     * @return org.springframework.core.task.TaskExecutor 线程池
     */
    static TaskExecutor toTaskExecutor(ThreadPoolProperties executorProperties) {
        return toTaskExecutor(new ThreadPoolExecutor.CallerRunsPolicy(), executorProperties);
    }

    /**
     * 线程池通用配置详情
     *
     * @param rejectedExecutionHandler 任务拒绝策略
     * @param executorProperties       线程池通用配置详情
     * @return org.springframework.core.task.TaskExecutor 线程池
     */
    static TaskExecutor toTaskExecutor(RejectedExecutionHandler rejectedExecutionHandler, ThreadPoolProperties executorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.initialize();
        return executor;
    }
}
