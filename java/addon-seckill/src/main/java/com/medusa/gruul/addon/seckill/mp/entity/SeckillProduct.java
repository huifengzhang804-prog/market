package com.medusa.gruul.addon.seckill.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_seckill_product", autoResultMap = true)
public class SeckillProduct extends BaseEntity {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * sku 规格
     */
    private String specs;

    /**
     * 活动库存
     */
    private Long stock;

    /**
     * 活动价格
     */
    private Long price;
}
