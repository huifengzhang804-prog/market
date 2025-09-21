package com.medusa.gruul.addon.freight.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 计费方式枚举
 * @author xiaoq
 */
@Getter
@RequiredArgsConstructor
public enum ValuationModel {
    /**
     * 件数
     */
    PKGS(0),

    /**
     * 重量
     */
    WEIGHT(1);
    @EnumValue
    private final Integer value;
}
