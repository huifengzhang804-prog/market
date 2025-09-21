package com.medusa.gruul.search.api.model;

import com.medusa.gruul.common.model.enums.OrderType;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/22
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ProductActivityUnbind implements Serializable {
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

}
