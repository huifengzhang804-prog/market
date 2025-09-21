package com.medusa.gruul.common.xxl.job;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.Xxl;
import com.medusa.gruul.common.xxl.job.service.impl.JobServiceImpl;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * xxl job 自动装配
 *
 * @author 张治保
 * date 2023/3/17
 */
@Slf4j
@Import(JobServiceImpl.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoconfigure {

    private final Environment environment;

    @Bean
    @ConditionalOnProperty(prefix = "gruul.xxl-job.executor", name = "enabled", havingValue = "true", matchIfMissing = true)
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties prop) {
        String serviceName = environment.getProperty("spring.application.name");
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(prop.getAdmin().getAdminAddresses());
        XxlJobProperties.ExecutorProperties executorProp = prop.getExecutor();
        executor.setAppname(this.getAppName(executorProp.getAppName(), serviceName));
        executor.setIp(Xxl.GATEWAY_XXL_EXEC_PATH);
        executor.setPort(this.getPort());
        executor.setAccessToken(executorProp.getAccessToken());
        executor.setLogPath(executorProp.getLogPath());
        executor.setLogRetentionDays(executorProp.getLogRetentionDays());
        executor.setAddress(this.getAddress(executorProp.getRouterAddress(), serviceName));
        return executor;
    }

    /**
     * 获取执行器 端口
     *
     * @return 端口
     */
    private int getPort() {
        Integer serverPort = environment.getProperty("server.port", Integer.class);
        serverPort = serverPort == null ? 8080 : serverPort;
        return serverPort + Xxl.GATEWAY_XXL_EXEC_PORT_OFFSET;
    }

    /**
     * 获取执行器名称
     *
     * @param appName     配置名称
     * @param serviceName 服务名称
     * @return 执行器名称
     */
    private String getAppName(String appName, String serviceName) {
        if (StrUtil.isEmpty(appName)) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = ArrayUtil.isEmpty(activeProfiles) ? "dev" : activeProfiles[0];
            appName = activeProfile + "-" + serviceName;
        }
        return appName;
    }

    /**
     * 获取执行器调用地址
     *
     * @param routerAddress 路由地址
     * @param serviceName   服务名称
     * @return 执行器调用地址
     */
    private String getAddress(String routerAddress, String serviceName) {
        Assert.isFalse(StrUtil.isBlank(routerAddress), "xxl-job executor address is null");
        routerAddress = routerAddress.trim();
        String property = environment.getProperty("gruul.single");
        boolean isSingle = StrUtil.isNotEmpty(property) && Boolean.parseBoolean(property);
        if (isSingle) {
            return routerAddress;
        }
        return (routerAddress.endsWith("/") ? routerAddress : (routerAddress + "/")) + serviceName + Xxl.GATEWAY_XXL_EXEC_PATH;
    }
}
