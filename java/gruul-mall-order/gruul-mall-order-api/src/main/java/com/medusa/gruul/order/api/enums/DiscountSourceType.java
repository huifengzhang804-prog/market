package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 产生的优惠项的类型
 *
 * @author 张治保
 * date 2022/6/8
 */
@Getter
@RequiredArgsConstructor
public enum DiscountSourceType {
    /**
     * 平台优惠券
     */
    PLATFORM_COUPON(0, Boolean.TRUE),

    /**
     * 店铺优惠券
     */
    SHOP_COUPON(1, Boolean.FALSE),

    /**
     * 会员抵扣
     */
    MEMBER_DEDUCTION(2, Boolean.TRUE),

    /**
     * 满减
     */
    FULL_REDUCTION(3, Boolean.FALSE),

    /**
     * 消费返利
     */
    CONSUMPTION_REBATE(4, Boolean.TRUE);


    @EnumValue
    private final Integer value;

    /**
     * 是否是平台优惠
     */
    private final boolean platform;
}
