package com.medusa.gruul.addon.seckill.component;

import com.medusa.gruul.addon.seckill.model.SeckillConf;
import com.medusa.gruul.addon.seckill.model.SeckillConst;
import com.medusa.gruul.addon.seckill.model.bo.JobId;
import com.medusa.gruul.addon.seckill.model.enums.SeckillJobType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.addon.CommonConfigService;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Component
public class SeckillConfService extends CommonConfigService<SeckillConf> {

    private final JobService jobService;

    public SeckillConfService(JobService jobService) {
        super("seckill",
                new SeckillConf()
                        .setDuration(8));
        this.jobService = jobService;
    }


    @Override
    @Redisson(name = SeckillConst.SECKILL_ACTIVITY_CONF_LOCK_KEY)
    public void setBefore(SeckillConf seckillConf) {
        //鉴权
        boolean permit = ISecurity.matcher()
                .role(Roles.SUPER_ADMIN)
                .or(matcher -> matcher.role(Roles.SUPER_CUSTOM_ADMIN).perm("secondsKill"))
                .match();
        if (!permit) {
            throw SecureCodes.PERMISSION_DENIED.exception();
        }
        //是否存在进行中、未开始的活动
        Long size = RedisUtil.getRedisTemplate()
                .opsForZSet()
                .size(SeckillConst.ROUND_CACHE_KEY);
        if (size != null && size > CommonPool.NUMBER_ZERO) {
            throw new GlobalException("未开始和进行中的秒杀活动数=0时才能修改");
        }
    }


    @Override
    @SneakyThrows
    public void setAfter(SeckillConf seckillConf, Throwable ex) {
        if (ex != null) {
            throw ex;
        }
        //生成秒杀定时任务
        Integer duration = seckillConf.getDuration();
        //开始时间
        int startId = jobService.add(SeckillJobType.START.toXxlJobInfo(duration));
        //结束时间
        int endId = jobService.add(SeckillJobType.STOP.toXxlJobInfo(duration));
        //删除之前的定时任务
        JobId preJobId = RedisUtil.getCacheMap(SeckillConst.XXL_JOB_ID_CACHE_KEY, JobId.class);
        if (preJobId != null) {
            jobService.remove(preJobId.getStartId());
            jobService.remove(preJobId.getEndId());
        }
        //缓存任务 id
        RedisUtil.setCacheMap(
                SeckillConst.XXL_JOB_ID_CACHE_KEY,
                new JobId().setStartId(startId).setEndId(endId)
        );
    }
}
