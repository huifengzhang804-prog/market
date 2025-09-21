package com.medusa.gruul.addon.seckill.model;

/**
 * @author 张治保
 * @since 2024/5/29
 */
public interface SeckillConst {

    /**
     * xxl job 任务 id 缓存 key
     */
    String XXL_JOB_ID_CACHE_KEY = "addon:seckill:xxl:job:id";

    /**
     * 秒杀活动配置更新 锁 key
     */
    String SECKILL_ACTIVITY_CONF_LOCK_KEY = "addon:seckill:activity:config:lock";

    /**
     * 场次缓存 key
     */
    String ROUND_CACHE_KEY = "addon:seckill:round";

    /**
     * 统计场次活动数量缓存 key 如果数量为零讲删除场次缓存
     */
    String ROUND_ACTIVITY_COUNT_CACHE_KEY = "addon:seckill:round:count";

    /**
     * 秒杀活动信息缓存 key
     */
    String SECKILL_ACTIVITY_CACHE_KEY = "addon:seckill:activity:detail";

    /**
     * 秒杀活动信息缓存 key 前缀
     */
    String SECKILL_ACTIVITY_PRODUCT_CACHE_KEY = "addon:seckill:activity:products";

}
