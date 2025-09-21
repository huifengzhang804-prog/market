package com.medusa.gruul.addon.bargain.model.vo;

import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 砍价列表VO
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainInfoVO {

    /**
     * 砍价id
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 砍价活动名称
     */
    private String name;


    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    private LocalDateTime endTime;

    /**
     * 活动状态
     */
    private ActivityStatus status;

    /**
     * 活动商品数
     */
    private Integer productNum;

    /**
     * 支付订单数
     */
    private Integer payOrder;
}