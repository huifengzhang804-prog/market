package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: WuDi
 * @date: 2022/10/10
 */

@Getter
@RequiredArgsConstructor
public enum CapitalFlowsType {


    /**
     * 收入
     */
    INCOME(0),
    /**
     * 支出
     */
    SPENDING(1);

    @EnumValue
    private final Integer value;

}
