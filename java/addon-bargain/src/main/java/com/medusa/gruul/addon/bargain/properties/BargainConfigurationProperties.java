package com.medusa.gruul.addon.bargain.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wudi
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.bargain")
public class BargainConfigurationProperties {


    /**
     * 线程池配置
     */
    private TaskThreadPool threadPool = new TaskThreadPool();

    @Getter
    @Setter
    public static class TaskThreadPool {

            /**
            * 线程池线程名前缀
            */
            private String threadNamePrefix = "Bargain-Future";
            /**
            * 核心线程数
            */
            private int corePoolSize = 10;
            /**
            * 最大线程数
            */
            private int maxPoolSize = 25;
            /**
            * 队列容量
            */
            private int queueCapacity = 300;
            /**
            * 线程池维护线程所允许的空闲时间
            */
            private int keepAliveSeconds = 60;
    }
}
