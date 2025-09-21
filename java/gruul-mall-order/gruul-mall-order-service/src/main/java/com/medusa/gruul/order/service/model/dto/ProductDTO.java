package com.medusa.gruul.order.service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

/**
 * 购买的商品 以及 商品sku 信息
 *
 * @author 张治保
 * date 2022/3/7
 */
@Getter
@Setter
@ToString
public class ProductDTO {
    /**
     * 购买的商品id
     */
    @NotNull
    private Long id;
    /**
     * 选择购买的商品SKU 信息
     */
    @NotNull
    private Long skuId;
    /**
     * 选购数量
     */
    @NotNull
    @Min(1)
    private Integer num;

    /**
     * 商品属性
     */
    private Map<Long, Set<Long>> productFeaturesValue;
}
