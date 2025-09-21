package com.medusa.gruul.addon.bargain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 砍价商品详情
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainProductDetailVO implements Serializable {

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
     * skuId
     */
    private Long skuId;

    /**
     * sku库存
     */
    private Long skuStock;

    /**
     * 砍价底价
     */
    private Long floorPrice;


    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
