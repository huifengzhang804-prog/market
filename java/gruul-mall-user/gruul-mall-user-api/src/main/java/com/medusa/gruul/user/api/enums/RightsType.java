package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaoq
 * @Description
 * @date 2022-11-09 14:50
 */
@Getter
@RequiredArgsConstructor
public enum RightsType {
    /**
     * 商品抵扣
     */
    GOODS_DISCOUNT(1),

    /**
     * 积分加倍
     */
    INTEGRAL_MULTIPLE(2),

    /**
     * 物流优惠
     */
    LOGISTICS_DISCOUNT(3),

    /**
     * 优先发货
     */
    PRIORITY_SHIPMENTS(4),

    /**
     * 极速售后
     */
    QUICKNESS_AFS(5),

    /**
     * 专属客服
     */
    EXCLUSIVE_SERVICE(6),

    /**
     * 成长值翻倍
     */
    GROWTH_VALUE_MULTIPLE(7),

    /**
     * 自定义
     */
    USER_DEFINED(8);
    @EnumValue
    private final Integer value;
}
