package com.medusa.gruul.addon.seckill.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.addon.seckill.model.SeckillConst;
import com.medusa.gruul.addon.seckill.model.bo.JobId;
import com.medusa.gruul.addon.seckill.model.enums.SeckillJobType;
import com.medusa.gruul.addon.seckill.model.enums.SeckillStatus;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillActivityDao;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillActivity;
import com.medusa.gruul.addon.seckill.util.SeckillUtil;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Configuration
@RequiredArgsConstructor
public class ActivityJobHandler {

    private final JobService jobService;
    private final SeckillActivityDao seckillActivityDao;
    private final SeckillConfService seckillConfService;
    @SuppressWarnings("unused")
    private final RedisTemplate<String, Object> redisTemplate;


    @Bean
    public CommandLineRunner xxlJobInit() {
        return new CommandLineRunner() {
            @Override
            @Redisson(name = SeckillConst.SECKILL_ACTIVITY_CONF_LOCK_KEY)
            public void run(String... args) {
                //如果存在说明已经设置了定时任务
                Boolean exists = redisTemplate.hasKey(SeckillConst.XXL_JOB_ID_CACHE_KEY);
                if (BooleanUtil.isTrue(exists)) {
                    return;
                }
                //生成秒杀定时任务
                Integer duration = seckillConfService.get().getDuration();
                //开始时间
                int startId = jobService.add(SeckillJobType.START.toXxlJobInfo(duration));
                //结束时间
                int endId = jobService.add(SeckillJobType.STOP.toXxlJobInfo(duration));
                //缓存任务 id
                RedisUtil.setCacheMap(
                        SeckillConst.XXL_JOB_ID_CACHE_KEY,
                        new JobId().setStartId(startId).setEndId(endId)
                );

            }
        };
    }

    /**
     * 开启秒杀活动
     */
    @XxlJob(SeckillJobType.START_JOB_HANDLER)
    public void start() {
    }


    /**
     * 结束秒杀活动
     */
    @XxlJob(SeckillJobType.STOP_JOB_HANDLER)
    public void stop() {
        LocalDateTime now = LocalDateTime.now();
        //xxl job 可能会提前一秒钟执行 所以加上两秒 并取小时整数
        LocalDateTime endTime = now.plusSeconds(CommonPool.NUMBER_TWO)
                .truncatedTo(ChronoUnit.HOURS);
        List<SeckillActivity> activities = seckillActivityDao.lambdaQuery()
                .select(SeckillActivity::getStartTime, SeckillActivity::getShopId, SeckillActivity::getId)
                .eq(SeckillActivity::getStatus, SeckillStatus.OK)
                .le(SeckillActivity::getEndTime, endTime)
                .lt(SeckillActivity::getStartTime, now)
                .list();
        if (CollUtil.isEmpty(activities)) {
            return;
        }
        seckillActivityDao.lambdaUpdate()
                .set(SeckillActivity::getStatus, SeckillStatus.FINISHED)
                .eq(SeckillActivity::getStatus, SeckillStatus.OK)
                .le(SeckillActivity::getEndTime, endTime)
                .lt(SeckillActivity::getStartTime, now)
                .update();
        //删除该场次缓存
        Set<LocalDateTime> startTimes = CollUtil.newHashSet();
        Set<CurrentActivityKey> activityKeys = CollUtil.newHashSet();
        for (SeckillActivity activity : activities) {
            startTimes.add(activity.getStartTime());
            activityKeys.add(new CurrentActivityKey().setShopId(activity.getShopId()).setActivityId(activity.getId()));
        }
        SeckillUtil.clearAllOfRoundAndProducts(
                startTimes,
                activityKeys
        );
    }
}
