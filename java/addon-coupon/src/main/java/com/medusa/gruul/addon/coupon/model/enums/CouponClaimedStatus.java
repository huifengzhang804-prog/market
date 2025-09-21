package com.medusa.gruul.addon.coupon.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/2/28
 */
@Getter
@RequiredArgsConstructor
public enum CouponClaimedStatus {
    /**
     * 可以领取
     */
    CLAIM(1),
    /**
     * 领取达上限
     */
    LIMIT(2),
    /**
     * 已领完
     */
    FINISHED(3);

    @EnumValue
    private final Integer value;

}
