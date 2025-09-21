package com.medusa.gruul.addon.matching.treasure.model.vo;

import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单套餐商品
 */
@Getter
@Setter
public class SetMealProductOrderVO {

    /**
     * 活动id
     */
    private Long activityId;


    /**
     * 商品属性(0:主商品 1:搭配商品)
     */
    private ProductAttributes productAttributes;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * sku_id
     */
    private Long skuId;

    /**
     * sku库存
     */
    private Long skuStock;


}
