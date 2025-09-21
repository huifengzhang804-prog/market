package com.medusa.gruul.addon.seckill.model.vo;

import com.medusa.gruul.addon.seckill.model.enums.SeckillQueryStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/5/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillActivityVO implements Serializable {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动状态
     */
    private SeckillQueryStatus status;

    /**
     * 参与人数
     */
    private Long user;

    /**
     * 支付订单数
     */
    private Long payOrder;

    /**
     * 商品数
     */
    private Long product;

    /**
     * 违规原因
     */
    private String violation;
}
