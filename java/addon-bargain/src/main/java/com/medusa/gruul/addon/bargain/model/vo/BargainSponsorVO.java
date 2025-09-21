package com.medusa.gruul.addon.bargain.model.vo;

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
public class BargainSponsorVO {

    /**
     * 发起人id
     */
    private Long userId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 砍价活动id
     */
    private Long activityId;


    /**
     * 砍价订单id
     */
    private Long bargainOrderId;


}
