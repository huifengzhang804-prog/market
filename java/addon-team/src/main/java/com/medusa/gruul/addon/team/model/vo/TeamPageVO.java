package com.medusa.gruul.addon.team.model.vo;

import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TeamPageVO {

    /**
     * 活动id
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动状态
     */
    private TeamStatus status;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 订单数
     */
    private Integer orders;

    private TeamMode mode;
}
