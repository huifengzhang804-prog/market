package com.medusa.gruul.global.model.o;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 线程池通用配置详情
 *
 * @author 张治保
 * @since 2023/12/08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ThreadPoolProperties implements Serializable {

    /**
     * 线程池线程名前缀
     */
    private String threadNamePrefix = "Future";

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 2;

    /**
     * 线程存活时间长度
     */
    private int keepAliveSeconds = 60;

    /**
     * 任务队列长度
     */
    private int queueCapacity = 20;

}