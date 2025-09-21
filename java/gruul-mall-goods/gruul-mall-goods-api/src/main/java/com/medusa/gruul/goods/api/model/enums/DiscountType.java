package com.medusa.gruul.goods.api.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 折扣类型
 *
 * @author WuDi
 * date: 2022/11/15
 */

@Getter
@RequiredArgsConstructor
public enum DiscountType {

    /**
     * 会员
     */
    VIP("会员"),

    /**
     * 优惠劵
     */
    COUPON("优惠券"),

    /**
     * 满减
     */
    FULL("满减"),
    ;

    /**
     * 折扣描述
     * todo 后期修改为 i18n的方式
     */
    private final String desc;
}


