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
public class TeamOrderCount {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 参加人数
     */
    private Integer users = 0;

    /**
     * 应收金额
     */
    private Long amount = 0L;

    /**
     * 订单数
     */
    private Integer orders = 0;

    /**
     * 成团订单数
     */
    private Integer succeed = 0;
}
