package com.medusa.gruul.addon.team.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.enums.TeamProductStatus;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.vividsolutions.jts.util.Assert;
import jakarta.validation.constraints.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/16
 */
public interface TeamUtil {

    /**
     * 根据时间获取当前数据得分
     *
     * @param time 时间
     * @return 得分
     */
    static long getScoreByTime(@NotNull LocalDateTime time) {
        Assert.isTrue(time != null, "time can not be null");
        return LocalDateTimeUtil.toEpochMilli(time);
    }

    /**
     * 生成新团号
     *
     * @return 团号
     */
    static String teamNoGenerate() {
        return RedisUtil.no(TeamConst.NO_PREFIX, TeamConst.NO_KEY);
    }

    /**
     * 获取商品拼团活动缓存key
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品拼团活动缓存key
     */
    static String getTeamProductCacheKey(Long shopId, Long productId) {
        return RedisUtil.key(TeamConst.PRODUCT_TEAM_INFO_CACHE_KEY, shopId, productId);
    }

    /**
     * 获取已经开始的商品拼团信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品拼团信息
     */
    static TeamProductVO getProductOpenedTeam(Long shopId, Long productId) {
        //从缓存中取出活动数据
        Set<Object> range = RedisUtil.getRedisTemplate()
                .opsForZSet().range(
                        TeamUtil.getTeamProductCacheKey(shopId, productId),
                        0, 0
                );
        if (CollUtil.isEmpty(range)) {
            return new TeamProductVO().setTeamStatus(TeamProductStatus.NO);
        }
        //
        TeamProductVO product = FastJson2.convert(range.iterator().next(), TeamProductVO.class);
        return product.initStatus();
    }

    /**
     * 获取团号分布式锁锁key
     *
     * @param teamNo 团号
     * @return 锁key
     */
    static String getTeamLockKey(String teamNo) {
        return RedisUtil.key(TeamConst.TEAM_LOCK_KEY, teamNo);
    }


    /**
     * 团锁 加锁并运行任务
     *
     * @param redissonClient redisson客户端
     * @param teamNo         团号
     * @param task           任务
     */
    static void lockTeam(@NonNull RedissonClient redissonClient, String teamNo, Runnable task) {
        RLock lock = redissonClient.getLock(TeamUtil.getTeamLockKey(teamNo));
        lock.lock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }
}
