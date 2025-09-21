package com.medusa.gruul.goods.service.model.dto;

import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

/**
 * 商品详情请求参数
 *
 * @author 张治保
 * @since 2024/6/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductDetailDTO implements BaseDTO {

    /**
     * 店铺 id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品 id
     */
    @NotNull
    private Long productId;

    /**
     * sku id
     */
    private Long skuId;

    /**
     * 选择的 sku 的数量 当 skuid 不为空时必填
     */
    @Min(1)
    private Long num = 1L;

    /**
     * 选择的商品属性
     */
    private Map<Long, Set<Long>> features;

}
