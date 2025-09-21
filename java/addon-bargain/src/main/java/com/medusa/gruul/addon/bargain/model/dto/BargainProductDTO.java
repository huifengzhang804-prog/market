package com.medusa.gruul.addon.bargain.model.dto;

import com.medusa.gruul.storage.api.enums.StockType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainProductDTO {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 商品图片
     */
    @NotBlank
    private String productPic;

    /**
     * 商品名称
     */
    @NotBlank
    private String productName;

    /**
     * sku Id
     */
    @NotNull
    private Long skuId;

    /**
     * sku库存
     */
    private Long skuStock;

    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * sku价格
     */
    private Long skuPrice;
    /**
     * 库存
     */
    @NotNull
    private Long stock;

    /**
     * 规格名
     */
    private String skuName;

    /**
     * 砍价底价
     */
    @NotNull
    private Long floorPrice;

}
