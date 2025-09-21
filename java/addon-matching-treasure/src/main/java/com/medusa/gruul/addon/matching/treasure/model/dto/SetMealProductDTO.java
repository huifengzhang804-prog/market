package com.medusa.gruul.addon.matching.treasure.model.dto;

import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.storage.api.enums.StockType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SetMealProductDTO {

    /**
     * 套餐id
     */
    private Long setMealId;

    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    private Long productId;

    /**
     * 商品图片
     */
    @NotBlank(message = "商品图片不能为空")
    private String productPic;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    /**
     * 商品属性(0:主商品 1:搭配商品)
     */
    @NotNull(message = "商品属性不能为空")
    private ProductAttributes productAttributes;

    /**
     * sku_id
     */
    @NotNull(message = "skuId不能为空")
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
    @NotNull(message = "sku库存不能为空")
    private Long skuStock;

    /**
     * sku原价
     */
    @NotNull(message = "sku价格不能为空")
    private Long skuPrice;

    /**
     * 搭配价
     */
    @NotNull(message = "搭配价不能为空")
    private Long matchingPrice;

    /**
     * 搭配库存
     */
    @NotNull(message = "搭配库存不能为空")
    private Long matchingStock;

}
