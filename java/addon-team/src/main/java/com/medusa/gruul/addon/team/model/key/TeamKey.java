package com.medusa.gruul.addon.team.model.key;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class TeamKey implements Serializable {

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


    /**
     * 活动商品id
     */
    private Set<Long> productId;

}
