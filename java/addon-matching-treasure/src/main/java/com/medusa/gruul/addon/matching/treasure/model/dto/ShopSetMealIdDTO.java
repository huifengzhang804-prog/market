package com.medusa.gruul.addon.matching.treasure.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShopSetMealIdDTO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 套餐id
     */
    private Long setMealId;

}
