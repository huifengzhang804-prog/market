package com.medusa.gruul.addon.bargain.model.vo;

import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainActivityProductVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品id
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
     * sku Id
     */
    private Long skuId;

    /**
     * sku库存
     */
    private Long skuStock;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 规格名
     */
    private String skuName;


    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * sku价格
     */
    private Long skuPrice;

    /**
     * 砍价底价
     */
    private Long floorPrice;
}
