package com.medusa.gruul.common.xxl.job.model;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2023/3/17
 */
public enum ExecutorRouteStrategyEnum {
    /**
     * 第一个
     */
    FIRST,
    /**
     * 最后一个
     */
    LAST,
    /**
     * 轮询
     */
    ROUND,
    /**
     * 随机
     */
    RANDOM,
    /**
     * 一致性HASH
     */
    CONSISTENT_HASH,
    /**
     * 最不经常使用
     */
    LEAST_FREQUENTLY_USED,
    /**
     * 最久未使用
     */
    LEAST_RECENTLY_USED,
    /**
     * 忙碌转移
     */
    BUSYOVER,
    /**
     * 分片广播
     */
    SHARDING_BROADCAST
}
