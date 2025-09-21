package com.medusa.gruul.addon.freight.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * 物流公司状态
 *
 * @author xiaoq
 * @Description LogisticsCompanyStatus.java
 * @date 2022-06-06 09:42
 */
@Getter
@RequiredArgsConstructor
public enum LogisticsCompanyStatus {
    /**
     * 开启
     */
    OPEN(0),
    /**
     * 禁用
     */
    FORBIDDEN(1),
    ;

    @EnumValue
    private final Integer value;
}
