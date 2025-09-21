package com.medusa.gruul.service.uaa.service.properties;

import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * date 2022/10/24
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.uaa")
public class UaaProperties {



    /**
     * 普通 h5 二维码跳转链接 url
     */
    private String h5Url;

    /**
     * 扩展配置
     */
    private Extension extension = new Extension();

    /**
     * 线程池配置
     */
    private ThreadPoolProperties threadPool = new ThreadPoolProperties()
            .setThreadNamePrefix("UAA-Future")
            .setCorePoolSize(8)
            .setMaxPoolSize(16)
            .setKeepAliveSeconds(60)
            .setQueueCapacity(300);


    /**
     * 扩展配置
     */
    @Getter
    @Setter
    public static class Extension {

        /**
         * vue 是否开启了hash模式 是否使用# 路由
         */
        private boolean viewHash = true;

        /**
         * h5页面访问地址
         */
        private String h5Url = "https://prod.bgniao.cn/h5";

        /**
         * 消费者默认头像
         */
        private String defaultConsumerAvatar = "https://oss-tencent.bgniao.cn/head_portrait%20/images%20(3).png";

    }
}
