package com.medusa.gruul.order.api.addon.coupon;

import com.medusa.gruul.order.api.BuyerOrder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class OrderCouponParam extends BuyerOrder implements Serializable {


    /**
     * 店铺使用的优惠券map
     * key 店铺id 平台为0 ，value为优惠券id
     */
    @NotNull
    @Size(min = 1)
    private Map<Long, Long> shopCouponMap;

    /**
     * 店铺 对应的商品价格map
     * key 店铺id ，value map<商品id，商品总价>
     */
    @NotNull
    @Size(min = 1)
    private Map<Long, Map<Long, Long>> shopProductAmountMap;
}
