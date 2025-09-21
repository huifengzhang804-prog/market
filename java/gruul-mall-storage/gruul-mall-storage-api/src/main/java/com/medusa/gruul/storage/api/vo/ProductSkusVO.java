package com.medusa.gruul.storage.api.vo;

import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author WuDi date: 2022/11/14
 */
@Data
@Accessors(chain = true)
public class ProductSkusVO implements Serializable {

    /**
     * 商品id
     */
    private Long productId;


    /**
     * 商品名称
     */
    private String productName;


    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 展示图片
     */
    private String productPic;

    /**
     * 最低价
     */
    private Long lowestPrice;

    /**
     * 最高价
     */
    private Long highestPrice;


    /**
     * 商品sku
     */
    private List<SkuVO> skus;

    @Getter
    @Setter
    @ToString
    public static class SkuVO implements Serializable {

        /**
         * 商品id
         */
        private Long productId;
        /**
         * skuId
         */
        private Long skuId;

        /**
         * sku名称
         */
        private List<String> skuName;
        /**
         * sku价格
         */
        private Long skuPrice;

        /**
         * 原价
         */
        private Long price;

        /**
         * 库存类型
         */
        private StockType stockType;

        /**
         * 商品库存
         */
        private Long skuStock;

        /**
         * 销量
         */
        private Long salesVolume;
        /**
         * 规格图片
         */
        private String image;


    }

}
