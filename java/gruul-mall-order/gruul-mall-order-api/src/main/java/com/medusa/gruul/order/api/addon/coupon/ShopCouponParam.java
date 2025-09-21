package com.medusa.gruul.order.api.addon.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/11/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopCouponParam implements Serializable {


    /**
     * 店铺id 平台 shopId为0
     */
    @NotNull
    private Long shopId;

    /**
     * 优惠券id
     */
    @NotNull
    private Long couponId;


    /**
     * 商品与金额关系
     */
    private Map<Long, Long> productAmountMap;


}
