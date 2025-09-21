package com.medusa.gruul.afs.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: WuDi
 * @date: 2022/10/18
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.afs")
public class AfsConfigurationProperties {

    /**
     * 售后超时时间配置
     */
    private AfsTimeout afsTimeout = new AfsTimeout();

    /**
     * 线程池配置
     */
    private TaskThreadPool threadPool = new TaskThreadPool();
    

    @Getter
    @Setter
    public static class AfsTimeout {
        /**
         * 买家退货超时时间 单位：秒
         */
        private Long returnedTimeout = 7 * 24 * 60 * 60L;

        /**
         * 卖家确认收货超时时间 单位：秒
         */
        private Long confirmReturnedTimeout = 7 * 24 * 60 * 60L;
    }

    /**
     * 线程池配置详情
     */
    @Getter
    @Setter
    public static class TaskThreadPool {

        /**
         * 线程池线程名前缀
         */
        private String threadNamePrefix = "Afs-Future";
        /**
         * 核心线程数
         */
        private int corePoolSize = 10;
        /**
         * 最大线程数
         */
        private int maxPoolSize = 25;
        /**
         * 线程存活时间长度
         */
        private int keepAliveSeconds = 60;
        /**
         * 任务队列长度
         */
        private int queueCapacity = 800;
    }

}
