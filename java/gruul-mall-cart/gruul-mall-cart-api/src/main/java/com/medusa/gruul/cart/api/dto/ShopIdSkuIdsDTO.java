package com.medusa.gruul.cart.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/5/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopIdSkuIdsDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 购物车商品规格属性唯一ids
     */
    @NotNull
    @Size(min = 1)
    private Set<String> uniqueIds;


    /**
     * 店铺id的店铺的商品列表
     * key skuId value 商品规格属性列表
     */
    private Map<Long, List<List<String>>> skuSpecsMap;
}
