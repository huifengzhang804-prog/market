package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 概况交易类型
 *
 * @author xiaoq
 * @Description OverviewDealType.java
 * @date 2022-10-20 13:36
 */
@Getter
@RequiredArgsConstructor
public enum OverviewDealType {

    /**
     * 产品
     */
    PRODUCT(1),
    ;


    @EnumValue
    private final Integer value;
}
