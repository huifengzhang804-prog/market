package com.medusa.gruul.common.model.base;

import com.medusa.gruul.common.model.enums.OrderType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/29
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ActivityKey implements Serializable {

    /**
     * 活动类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;
}

