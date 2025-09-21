package com.medusa.gruul.user.service.config;

import com.medusa.gruul.user.service.task.ClearUserIntegralJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@RequiredArgsConstructor
public class UserJobHandlerConfig {

    private final ClearUserIntegralJob clearUserIntegralJob;

    /**
     * 清除用户积分定时任务
     */
    @XxlJob("clearUserIntegralJob")
    public void clearUserIntegralJob() {
        clearUserIntegralJob.execute();
    }
}