package com.medusa.gruul.addon.seckill.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/5/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class SeckillRoundProductVO implements Serializable {

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
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 划线价
     */
    private Long price;

    /**
     * 商品最低价
     */
    private Long minPrice;

    /**
     * 活动库存
     */
    private Long activityStock;
}
