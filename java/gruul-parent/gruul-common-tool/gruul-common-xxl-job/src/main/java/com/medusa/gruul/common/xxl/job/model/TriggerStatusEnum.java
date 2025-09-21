package com.medusa.gruul.common.xxl.job.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2023/3/20
 */
@Getter
@RequiredArgsConstructor
public enum TriggerStatusEnum {

    /**
     * 停止
     */
    STOP(0),
    
    /**
     * 启动
     */
    START(1);

    private final int code;
}
