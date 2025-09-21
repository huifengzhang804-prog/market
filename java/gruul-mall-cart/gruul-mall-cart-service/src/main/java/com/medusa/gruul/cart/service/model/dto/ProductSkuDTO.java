package com.medusa.gruul.cart.service.model.dto;

import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
public class ProductSkuDTO {

    /**
     * 店铺Id
     */
    @NotNull
    private Long shopId;
    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * sku id
     */
    @NotNull
    private Long skuId;

    /**
     * 数量
     */
    @NotNull
    @Min(value = 1)
    private Integer num;

    /**
     * 产品属性
     */
    private Set<ProductFeaturesValueDTO> productAttributes;
}
