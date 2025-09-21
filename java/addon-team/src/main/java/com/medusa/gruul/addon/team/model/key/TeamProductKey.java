package com.medusa.gruul.addon.team.model.key;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/29
 */
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class TeamProductKey implements Serializable {
    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;
}
