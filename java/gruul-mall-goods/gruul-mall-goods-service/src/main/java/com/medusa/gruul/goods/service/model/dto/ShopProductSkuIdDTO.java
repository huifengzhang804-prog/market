package com.medusa.gruul.goods.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 店铺商品SkuID
 *
 * @author wudi
 */
@Data
public class ShopProductSkuIdDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;
    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * skuId
     */
    @NotNull
    private Long skuId;

}
