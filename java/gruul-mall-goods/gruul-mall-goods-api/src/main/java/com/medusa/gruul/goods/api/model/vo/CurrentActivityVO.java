package com.medusa.gruul.goods.api.model.vo;

import com.medusa.gruul.common.model.enums.OrderType;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Administrator
 * @since 2024/2/26
 */
@Getter
@Setter
public class CurrentActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动类型
     */
    private OrderType activityType;

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
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;


}
