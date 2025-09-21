package com.medusa.gruul.addon.matching.treasure.config;


import com.medusa.gruul.addon.matching.treasure.constant.MatchingTreasureConstant;
import com.medusa.gruul.addon.matching.treasure.handler.MatchingTreasureJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MatchingTreasureXxlJobConfig {

    private final MatchingTreasureJobHandler matchingTreasureJobHandler;

    @XxlJob(MatchingTreasureConstant.SET_MEAL_XXL_JOB_HANDLER)
    public void matchingTreasureJobHandler() throws Exception {
        matchingTreasureJobHandler.execute();
    }
}
