package com.medusa.gruul.addon.full.reduction.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 查询条件里的状态状态
 */

@RequiredArgsConstructor
@Getter
public enum FullQueryStatus {

    /**
     * 正常状态
     */
    NOT_STARTED,

    /**
     * 进行中
     */
    IN_PROGRESS,

    /**
     * 活动结束
     */
    FINISHED,

    /**
     * 平台下架
     */
    OFF_SHELF,

    /**
     * 店铺下架
     */
    VIOLATION_OFF_SHELF


}
