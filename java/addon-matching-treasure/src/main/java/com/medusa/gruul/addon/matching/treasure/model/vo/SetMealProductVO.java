package com.medusa.gruul.addon.matching.treasure.model.vo;

import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SetMealProductVO {

    /**
     * 套餐id
     */
    private Long setMealId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品属性(0:主商品 1:搭配商品)
     */
    private ProductAttributes productAttributes;

    /**
     * sku_id
     */
    private Long skuId;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * sku库存
     */
    private Long skuStock;

    /**
     * sku原价
     */
    private Long skuPrice;

    /**
     * 搭配价
     */
    private Long matchingPrice;

    /**
     * 搭配库存
     */
    private Long matchingStock;
}
