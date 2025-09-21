package com.medusa.gruul.addon.freight.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 邮寄类型枚举
 *
 * @author xiaoq
 * @Description PostTypeEnum.java
 * @date 2022-05-26 10:03
 */
@Getter
@RequiredArgsConstructor
public enum PostTypeEnum {

    /**
     * 件数
     */
    PKGS(0),

    /**
     * 金额
     */
    MONEY(1),

    /**
     * 件数+金额
     */
    PKGS_MONEY(2),


    /**
     * 重量
     */
    WEIGHT(3),

    /**
     * 重量+金额
     */
    WEIGHT_MONEY(4);

    @EnumValue
    private final Integer value;
}
