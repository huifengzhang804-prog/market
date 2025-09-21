package com.medusa.gruul.common.redis.constant;

/**
 * @author 张治保
 * date 2022/3/7
 */
public interface RedisConstant {

    /**
     * redisson 批量锁 参数名（集合/数组 里的每一项的对象）
     */
    String BATCH_LOCK_ITEM = "item";

    /**
     * 单号前置key
     */
    String NO_KEY = "gruul:no";
}
