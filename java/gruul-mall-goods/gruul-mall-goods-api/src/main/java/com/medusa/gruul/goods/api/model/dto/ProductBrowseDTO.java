package com.medusa.gruul.goods.api.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 浏览商品DTO
 *
 * @author: WuDi
 * @date: 2022/9/5
 */
@Data
public class ProductBrowseDTO {

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;


}
