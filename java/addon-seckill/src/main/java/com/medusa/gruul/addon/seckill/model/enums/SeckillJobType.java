package com.medusa.gruul.addon.seckill.model.enums;

import com.medusa.gruul.addon.seckill.util.SeckillUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.xxl.job.JobParent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Getter
@RequiredArgsConstructor
public enum SeckillJobType implements JobParent<Integer> {


    START(
            "定时开启秒杀活动缓存",
            SeckillJobType.START_JOB_HANDLER,
            duration -> SeckillJobType.CRON_TEMPLATE.formatted(60 - SeckillUtil.NOW_TO_END_MIN_MINUTES, duration - 1, duration)
    ),

    STOP("定时结束秒杀活动，清楚该活动缓存",
            SeckillJobType.STOP_JOB_HANDLER,
            duration -> SeckillJobType.CRON_TEMPLATE.formatted(CommonPool.NUMBER_ZERO, CommonPool.NUMBER_ZERO, duration)
    );


    public static final String START_JOB_HANDLER = "seckillActivityStartJob";
    public static final String STOP_JOB_HANDLER = "seckillActivityStopJob";
    public static final String JOB_AUTHOR = "system";
    private static final String CRON_TEMPLATE = "0 %s %s/%s * * ?";
    /**
     * 任务描述
     */
    private final String desc;

    /**
     * 任务处理器
     */
    private final String handler;

    /**
     * 任务执行cron生成器
     */
    private final Function<Integer, String> cronGenerator;


    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String handler() {
        return handler;
    }

    @Override
    public String cronGenerator(Integer duration) {
        if (duration==24) {
            return "0 0 0 * * ?";
        }
        return cronGenerator.apply(duration);
    }
}
