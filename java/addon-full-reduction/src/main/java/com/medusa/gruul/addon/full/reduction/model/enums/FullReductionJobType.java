package com.medusa.gruul.addon.full.reduction.model.enums;

import com.medusa.gruul.common.xxl.job.CronHelper;
import com.medusa.gruul.common.xxl.job.JobParent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@RequiredArgsConstructor
public enum FullReductionJobType implements JobParent<LocalDateTime> {

    /**
     * 开始满减活动定时任务
     */
    START("开始满减活动定时任务", FullReductionJobType.START_JOB_HANDLER),

    /**
     * 结束满减活动定时任务
     */
    STOP("结束满减活动定时任务", FullReductionJobType.STOP_JOB_HANDLER);

    /**
     * 活动开始定时任务处理器名称
     */
    public static final String START_JOB_HANDLER = "startFullReductionActivity";

    /**
     * 活动结束定时任务处理器名称
     */
    public static final String STOP_JOB_HANDLER = "stopFullReductionActivity";

    private final String desc;

    private final String handler;

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String handler() {
        return handler;
    }

    @Override
    public String cronGenerator(LocalDateTime time) {
        return CronHelper.toCron(time);
    }
}
