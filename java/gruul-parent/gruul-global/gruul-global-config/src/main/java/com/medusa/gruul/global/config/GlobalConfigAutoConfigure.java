package com.medusa.gruul.global.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.global.config.helper.ExecutorHelper;
import com.medusa.gruul.global.config.spel.SpringElEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/3/23
 */
@Import(GlobalConfigAutoConfigure.ExecutorConfig.class)
@EnableConfigurationProperties(GlobalAppProperties.class)
public class GlobalConfigAutoConfigure implements ApplicationContextAware {


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringElEngine.setApplicationContext(applicationContext);
    }


    /**
     * 全局线程池配置类
     */
    @RequiredArgsConstructor
    public static class ExecutorConfig {
        private final GlobalAppProperties properties;

        /**
         * 全局普通线程池
         */
        @Bean(name = Global.GLOBAL_TASK_EXECUTOR)
        public TaskExecutor globalTaskExecutor() {
            return ExecutorHelper.toTaskExecutor(properties.getThreadPool());
        }

        /**
         * 全局可传递上下文的线程池
         */
        @Bean(name = Global.GLOBAL_EXECUTOR)
        public Executor globalExecutor(TaskExecutor globalTaskExecutor) {
            return TtlExecutors.getTtlExecutor(globalTaskExecutor);
        }
    }


}
