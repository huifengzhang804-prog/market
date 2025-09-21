package com.medusa.gruul.common.model.base;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 当前活动完整 key 仅在活动服务内部使用
 *
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class CurrentActivityKey implements Serializable {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 活动id
     */
    @NotNull
    private Long activityId;

}
