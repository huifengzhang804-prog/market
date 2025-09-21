package com.medusa.gruul.service.uaa.service.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.global.config.helper.ExecutorHelper;
import com.medusa.gruul.service.uaa.service.properties.UaaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/2/24
 */
@Configuration
@RequiredArgsConstructor
public class UaaConfig {


    /**
     * 异步执行线程池
     */
    @Bean
    public Executor uaaExecutor(UaaProperties uaaProperties) {
        return TtlExecutors.getTtlExecutor(
                ExecutorHelper.toTaskExecutor(uaaProperties.getThreadPool())
        );
    }


}
