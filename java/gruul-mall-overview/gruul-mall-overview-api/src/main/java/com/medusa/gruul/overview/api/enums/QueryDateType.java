package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 交易概况查询日期type
 *
 * @author xiaoq
 * @Description QueryDateType.jaca
 * @date 2022-10-20 18:54
 */
@Getter
@RequiredArgsConstructor
public enum QueryDateType {

    /**
     * 当日
     */
    TODAY(0),

    /**
     * 近一周
     */
    NEARLY_A_WEEK(1),

    /**
     * 进一个月
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
