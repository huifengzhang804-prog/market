package com.medusa.gruul.addon.bargain.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.api.enums.StockType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;


/**
 * 砍价商品
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_bargain_product")
public class BargainProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * sku价格
     */
    private Long skuPrice;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 库存类型
     */
    @NotNull
    private StockType stockType;

    /**
     * 规格名
     */
    private String skuName;

    /**
     * 砍价底价
     */
    private Long floorPrice;
}
