package com.medusa.gruul.order.api.addon.coupon;

import com.medusa.gruul.order.api.BuyerOrder;
import com.medusa.gruul.order.api.entity.OrderDiscount;
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
public class CouponResponse extends BuyerOrder implements Serializable {

    /**
     * 店铺优惠信息
     * key 店铺id value 优惠金额与信息
     */
    private Map<Long, OrderDiscount> orderDiscounts;
}
