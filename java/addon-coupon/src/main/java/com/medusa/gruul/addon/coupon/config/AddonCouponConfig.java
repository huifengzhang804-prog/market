package com.medusa.gruul.addon.coupon.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.addon.coupon.properties.CouponConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 张治保
 * date 2022/11/3
 */
@Configuration
@RequiredArgsConstructor
public class AddonCouponConfig {

    /**
     * 异步执行线程池
     */
    @Bean
    public Executor couponCompletableTaskExecutor(CouponConfigurationProperties couponConfigurationProperties) {
        CouponConfigurationProperties.TaskThreadPool taskThreadPool = couponConfigurationProperties.getThreadPool();
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
