package com.medusa.gruul.addon.matching.treasure.handler;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealKeyDTO;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.rpc.StorageActivityRpcService;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author wudi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingTreasureJobHandler {

    private final StorageActivityRpcService storageActivityRpcService;

    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        String jobParam = XxlJobHelper.getJobParam();
        SetMealKeyDTO setMealKey = JSON.parseObject(jobParam, SetMealKeyDTO.class);
        log.info("{} 定时任务开始", jobParam);
        if (StrUtil.isEmpty(jobParam)) {
            return;
        }
        storageActivityRpcService.activityClose(
                Set.of(
                        new ActivityCloseKey()
                                .setActivityType(OrderType.PACKAGE)
                                .setShopId(setMealKey.getShopId())
                                .setActivityId(setMealKey.getSetMealId())
                )
        );

    }
}
