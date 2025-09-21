package com.medusa.gruul.addon.coupon.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/5
 */
@Getter
@Setter
@ToString
public class GiftsToUserDTO {

    /**
     * 优惠券详情
     */
    @NotNull
    @Valid
    private CouponDTO coupon;

    /**
     * 用户id列表
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> userIds;

    /**
     * 是否是平台赠送
     */
    private boolean isPlatform;

}
