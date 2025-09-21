package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author xiaoq
 * @Description 变化类型
 * @date 2022-09-28 13:25
 */
@Getter
@RequiredArgsConstructor
public enum ChangeType {
    /**
     * 增加
     */
    INCREASE(0),

    /**
     * 减少
     */
    REDUCE(1),
    ;


    @EnumValue
    private final Integer type;

}
