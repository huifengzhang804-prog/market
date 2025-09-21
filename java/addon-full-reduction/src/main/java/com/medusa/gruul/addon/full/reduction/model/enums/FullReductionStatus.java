package com.medusa.gruul.addon.full.reduction.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum FullReductionStatus {

    /**
     * 正常状态
     */
    OK(1, false),

    /**
     * 活动结束
     */
    FINISHED(2, false),

    /**
     * 平台下架
     */
    OFF_SHELF(3, true),

    /**
     * 店铺下架
     */
    VIOLATION_OFF_SHELF(4, true);


    /**
     * db value
     */
    @EnumValue
    private final Integer value;

    /**
     * 是否已下架
     */
    private final boolean shelf;


}
