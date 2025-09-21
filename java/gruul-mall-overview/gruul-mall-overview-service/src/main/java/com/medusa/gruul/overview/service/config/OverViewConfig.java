package com.medusa.gruul.overview.service.config;

import com.medusa.gruul.overview.service.model.constants.OverViewConstants;
import com.medusa.gruul.overview.service.modules.export.listener.RedisMessageListener;
import com.medusa.gruul.overview.service.properties.OverViewConfigurationProperties;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.service.config
 * @author:jipeng
 * @createTime:2024/1/6 9:56
 * @version:1.0
 */
@Configuration
@EnableConfigurationProperties(OverViewConfigurationProperties.class)
public class OverViewConfig  {


    @Resource
    private OverViewConfigurationProperties overViewConfigurationProperties;

//    @Bean
//    public TaskExecutor overViewTaskExecutor() {
//        OverViewConfigurationProperties.TaskThreadPool taskThreadPool = overViewConfigurationProperties.getThreadPool();
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setThreadNamePrefix(taskThreadPool.getThreadNamePrefix());
//        executor.setCorePoolSize(taskThreadPool.getCorePoolSize());
//        executor.setMaxPoolSize(taskThreadPool.getMaxPoolSize());
//        executor.setQueueCapacity(taskThreadPool.getQueueCapacity());
//        executor.setKeepAliveSeconds(taskThreadPool.getKeepAliveSeconds());
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }

    // 配置 MessageListenerContainer
    @Bean
    public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                  RedisMessageListener redisMessageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        // 订阅频道
        container.addMessageListener(redisMessageListener,
                new ChannelTopic(OverViewConstants.DOWN_LOAD_FILE_SSE_CHANNEL));

        return container;
    }



}
