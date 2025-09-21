package com.medusa.gruul.addon.matching.treasure.model.vo;


import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class SetMealProductDetailVO {


    /**
     * 产品id
     */
    private Long productId;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * 商品属性(0:主商品 1:搭配商品)
     */
    private ProductAttributes productAttributes;

    /**
     * 最多可省
     */
    private Long saveAtLeast;

    /**
     * 套餐商品规格
     */
    private List<SetMealProductSkuDetailVO> setMealProductSkuDetails;


    @Data
    public static class SetMealProductSkuDetailVO {
        /**
         * sku_id
         */
        private Long skuId;

        /**
         * 规格名称
         */
        private List<String> skuName;

        /**
         * 搭配库存
         */
        private Long matchingStock;

        /**
         * 库存类型 1 无限库存 2 有限
         */
        private StockType stockType;

        /**
         * 最多可省
         */
        private Long saveAtLeast;

        /**
         * sku原价
         */
        private Long matchingPrice;

        /**
         * sku
         */
        private StorageSku storageSku;
    }

}
