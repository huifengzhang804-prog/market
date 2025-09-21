package com.medusa.gruul.addon.team.model.count;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@Setter
@Accessors(chain = true)
public class TeamProductCount {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品数量
     */
    private Integer productNum;
}
