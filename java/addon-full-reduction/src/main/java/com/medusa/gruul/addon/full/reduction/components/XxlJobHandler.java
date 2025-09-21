package com.medusa.gruul.addon.full.reduction.components;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionJobType;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionStatus;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReduction;
import com.medusa.gruul.addon.full.reduction.util.FullReductionUtil;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/6/18
 */
@Component
@RequiredArgsConstructor
public class XxlJobHandler {

    private final FullReductionDao fullReductionDao;

    @XxlJob(FullReductionJobType.START_JOB_HANDLER)
    public void startFullReductionActivity() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isEmpty(jobParam)) {
            return;
        }
        CurrentActivityKey key = JSON.parseObject(jobParam, CurrentActivityKey.class);
        Long shopId = key.getShopId();
        Long activityId = key.getActivityId();
        FullReduction one = fullReductionDao.lambdaQuery()
                .eq(FullReduction::getShopId, shopId)
                .eq(FullReduction::getId, activityId)
                .eq(FullReduction::getStatus, FullReductionStatus.OK)
                .one();
        if (one == null) {
            return;
        }
        FullReductionUtil.cacheActivity(
                shopId,
                activityId,
                one.getRules(),
                one.getProductType(),
                one.getProducts().keySet(),
                Duration.between(LocalDateTime.now(), one.getEndTime())
        );
    }

    @XxlJob(FullReductionJobType.STOP_JOB_HANDLER)
    public void stopFullReductionActivity() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StrUtil.isEmpty(jobParam)) {
            return;
        }
        CurrentActivityKey key = JSON.parseObject(jobParam, CurrentActivityKey.class);
        Long shopId = key.getShopId();
        Long activityId = key.getActivityId();
        RedisUtil.doubleDeletion(
                () -> fullReductionDao.lambdaUpdate()
                        .set(FullReduction::getStatus, FullReductionStatus.FINISHED)
                        .eq(FullReduction::getShopId, shopId)
                        .eq(FullReduction::getId, activityId)
                        .eq(FullReduction::getStatus, FullReductionStatus.OK)
                        .update(),
                () -> FullReductionUtil.deleteActivity(shopId, activityId)
        );
    }
}
