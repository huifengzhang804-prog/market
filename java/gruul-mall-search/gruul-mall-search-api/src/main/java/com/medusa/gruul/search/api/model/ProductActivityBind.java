package com.medusa.gruul.search.api.model;

import com.medusa.gruul.common.model.enums.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductActivityBind implements Serializable {

    /**
     * 活动类型
     */
    @NotNull
    private OrderType activityType;

    /**
     * 活动id
     */
    @NotNull
    private Long activityId;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> productIds;

    /**
     * 活动开始时间 开始时间需小于结束时间
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 活动结束时间 结束时间需大于开始时间
     */
    @NotNull
    private LocalDateTime endTime;
}
