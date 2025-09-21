package com.medusa.gruul.addon.bargain.config;

import com.medusa.gruul.addon.bargain.constant.BargainConstant;
import com.medusa.gruul.addon.bargain.handler.BargainXxlJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author wudi
 */
@Configuration
@RequiredArgsConstructor
public class BargainXxlJobConfig {

    private final BargainXxlJobHandler bargainXxlJobHandler;

    @XxlJob(BargainConstant.BARGAIN_XXL_JOB_HANDLER)
    public void bargainXxlJobHandler() throws Exception {
        bargainXxlJobHandler.execute();
    }
}
