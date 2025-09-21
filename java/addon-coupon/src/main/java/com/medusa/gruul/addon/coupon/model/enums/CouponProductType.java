package com.medusa.gruul.addon.coupon.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券作用的商品类型
 *
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@RequiredArgsConstructor
public enum CouponProductType {

    /**
     * 全部商品
     */
    ALL(1, Boolean.FALSE, Boolean.TRUE),

    /**
     * 店铺全部商品
     */
    SHOP_ALL(2, Boolean.FALSE, Boolean.FALSE),

    /**
     * 指定商品
     */
    ASSIGNED(3, Boolean.TRUE, Boolean.FALSE),

    /**
     * 指定商品不生效
     */
    ASSIGNED_NOT(4, Boolean.TRUE, Boolean.FALSE);

    @EnumValue
    private final Integer value;

    /**
     * 是否指定商品 不论 指定生效还是指定不生效
     */
    private final Boolean isAssigned;

    /**
     * 是否是平台操作
     */
    private final Boolean isPlatform;

}
