package com.medusa.gruul.order.api.addon.fullreduction;

import com.medusa.gruul.order.api.entity.OrderDiscount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FullReductionResponse implements Serializable {
    /**
     * 店铺优惠信息
     * key 店铺id value 优惠金额与信息
     */
    private Map<Long, OrderDiscount> orderDiscounts;




}
