package com.medusa.gruul.common.xxl.job.model;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {

    /**
     * 串行
     */
    SERIAL_EXECUTION,
    /*CONCURRENT_EXECUTION("并行"),*/
    /**
     * 丢弃后续调度
     */
    DISCARD_LATER,

    /**
     * 覆盖之前调度
     */
    COVER_EARLY
}
