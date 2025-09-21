package com.medusa.gruul.storage.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 商品sku价格DTO
 *
 * @author xiaoq
 * @Description ProductSkuPriceDTO.java
 * @date 2023-06-19 11:27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuPriceDTO {

    /**
     * 商品sku id
     */
    @NotNull
    private Long skuId;

    /**
     * 原价 单位 豪 1豪 = 0.01分
     */
    @NotNull
    private Long price;

    /**
     * 真实销售价 单位豪 1豪 = 0.01分
     */
    @NotNull
    private Long salePrice;
}
