package com.medusa.gruul.storage.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 商品价格更新消息 DTO
 *
 * @author 张治保
 * @since 2024/9/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductPriceUpdateDTO implements Serializable {
    /**
     * 店铺 Id集合
     * 如果是代销商品 可能被多家店铺代销 所以需要更新多个店铺的价格 所以是集合
     * 其它情况 只需要更新一个店铺的价格
     */
    private Set<Long> shopIds;

    /**
     * 商品 Id
     */
    private Long productId;

    /**
     * 商品 sku 价格
     * key 是 skuId  value 是 商品价格
     */
    private Map<Long, SkuPriceDTO> skuPriceMap;

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class SkuPriceDTO implements Serializable {
        /**
         * 商品原价（划线价） 单位 豪 1豪 = 0.01分
         */
        private Long price;

        /**
         * 商品销售价 单位 豪 1豪 = 0.01分
         */
        private Long salePrice;
    }

}
