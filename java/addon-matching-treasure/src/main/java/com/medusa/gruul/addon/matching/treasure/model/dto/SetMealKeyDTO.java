package com.medusa.gruul.addon.matching.treasure.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author xiaoq
 * @since 2024/5/10
 *  套餐keyDTO
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SetMealKeyDTO {

    /**
     * 套餐id
     */
    private  Long setMealId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 套餐商品ids
     */
    private Set<Long> productIds;

}
