package com.medusa.gruul.addon.coupon.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * 优惠券状态
 *
 * @author 张治保
 * date 2022/11/3
 */
@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    /**
     * 正常
     */
    OK(1, coupon -> coupon.setStatusText(
            coupon.finished() ? "已结束" :
                    coupon.notStated() ? "未开始" : "进行中"
    )),

    /**
     * 已下架-- 平台下架
     */
    BANED(2, coupon -> coupon.setStatusText("违规下架")),
    /**
     * 已下架-- 店铺下架
     */

    SHOP_BANED(3, coupon -> coupon.setStatusText("已下架")),

    ;

    @EnumValue
    private final Integer value;
    /**
     * 描述
     */
    private final Consumer<Coupon> descCheckFun;
}
