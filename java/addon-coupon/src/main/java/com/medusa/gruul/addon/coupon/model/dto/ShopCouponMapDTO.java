package com.medusa.gruul.addon.coupon.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/11/3
 */
@Getter
@Setter
@ToString
public class ShopCouponMapDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 优惠券id
     */
    @NotNull
    private Long couponId;
    /**
     * 违规原因
     */
    private String violationReason;
}
