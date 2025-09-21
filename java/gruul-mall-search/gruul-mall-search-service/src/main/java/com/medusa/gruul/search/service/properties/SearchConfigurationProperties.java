package com.medusa.gruul.search.service.properties;

import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WuDi
 * date 2022/10/31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.search")
public class SearchConfigurationProperties {

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPool = new ThreadPoolProperties()
            .setThreadNamePrefix("Search-Future")
            .setCorePoolSize(10)
            .setMaxPoolSize(25)
            .setKeepAliveSeconds(60)
            .setQueueCapacity(800);

    /**
     * 单个文件上传大小限制
     */
    private int maxFileSize;

    /**
     * 上传文件总大小限制
     */
    private int maxRequestSize;


}
