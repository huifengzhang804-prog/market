package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>日期范围枚举</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum DateRangeType {

    /**
     * 当天
     */
    TODAY(0),

    /**
     * 最近一周
     */
    NEARLY_A_WEEK(1),

    /**
     * 最近一个月
     */
    NEARLY_A_MONTH(2),

    /**
     * 自定义时间去 开始结束时间
     */
    CUSTOM(3),

    ;

    @EnumValue
    private final Integer value;
}
