package com.medusa.gruul.addon.coupon.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券领券类型
 */
@Getter
@RequiredArgsConstructor
public enum CouponCollectionType {

    /**
     * 手动领取
     */
    SELF_COLLECTION(1,"手动领取"),

    /**
     * 平台赠送
     */
    PLATFORM_GIFT(2,"平台赠送"),

    /**
     * 店铺赠送
     */
    SHOP_GIFT(3,"店铺赠送");

    @EnumValue
    private final Integer code;

    private final String text;

}
