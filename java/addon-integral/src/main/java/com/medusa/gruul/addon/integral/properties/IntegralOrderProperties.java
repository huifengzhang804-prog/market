package com.medusa.gruul.addon.integral.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author miskw
 * @date 2023/7/22
 * @describe 描述
 */
@Data
@ConfigurationProperties("gruul.addon.integral")
public class IntegralOrderProperties {

    /**
     * 超时时间配置
     */
    private OrderTimeout timeout;

    /**
     * 线程池配置
     */
    private TaskThreadPool threadPool = new TaskThreadPool();

    @Data
    public static class OrderTimeout {
        /**
         * 订单支付超时时间 单位秒
         * 默认15分钟
         */
        private Long timeout = 900L;
        /**
         * 已发货订单自动完成时间 单位秒
         * 默认3天
         */
        private Long confirmTimeout = 259200L;
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
        private String threadNamePrefix = "Integral-Future";
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
