package com.medusa.gruul.common.xxl.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job 配置.
 *
 * @author 张治保
 */
@Data
@ConfigurationProperties(prefix = "gruul.xxl-job")
public class XxlJobProperties {

    /**
     * admin 配置
     */
    private AdminProperties admin = new AdminProperties();

    /**
     * executor 配置.
     */
    private ExecutorProperties executor = new ExecutorProperties();


    @Data
    public static class AdminProperties {
        /**
         * xxl job admin 地址.
         */
        private String adminAddresses = "http://127.0.0.1:8080/xxl-job-admin";
    }


    @Data
    public static class ExecutorProperties {

        /**
         * 是否启用该执行器
         */
        private boolean enabled = true;

        /**
         * xxl job executor id
         */
        private Integer id = 1;
        /**
         * app name
         */
        private String appName = "";

        /**
         * xxl 注册到 xxl job 服务端的路由地址.
         */
        private String routerAddress;
        /**
         * xxl job admin registry access token.
         */
        private String accessToken;
        /**
         * xxl job log files path.
         */
        private String logPath = "logs/applogs/xxl-job/jobhandler";
        /**
         * xxl job log files retention days.
         */
        private Integer logRetentionDays = 30;


    }
}
