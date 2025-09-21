package com.medusa.gruul.common.custom.aggregation.classify.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-10 09:32
 */
@Getter
@RequiredArgsConstructor
public enum CategoryLevel {
    /**
     * 一级类目
     */
    LEVEL_1(0),
    /**
     * 二级类目
     */
    LEVEL_2(1),
    /**
     * 三级类目
     */
    LEVEL_3(2);
    @EnumValue
    private final Integer value;
}
