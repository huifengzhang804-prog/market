package com.medusa.gruul.addon.matching.treasure.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 套餐商品
 *
 * @author WuDi
 * @since 2023-02-27
 */
@Getter
@Setter
@TableName("t_set_meal_product")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SetMealProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐id
     */
    private Long setMealId;

    /**
     * 店铺id
     */
    private Long shopId;

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
     * sku_id
     */
    private Long skuId;

    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * sku库存
     */
    private Long skuStock;

    /**
     * sku原价
     */
    private Long skuPrice;

    /**
     * 搭配价
     */
    private Long matchingPrice;

    /**
     * 搭配库存
     */
    private Long matchingStock;

    /**
     * 最多可省
     */
    @TableField(exist = false)
    private Long saveAtLeast;

}
