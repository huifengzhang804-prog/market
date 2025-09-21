package com.medusa.gruul.addon.team.model.vo;

import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/3/15
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamOrderSummaryVO {

    /**
     * 活动名称。
     */
    private String name;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 订单号。
     */
    private String orderNo;

    /**
     * 团长的用户ID。
     */
    private Long commanderId;

    /**
     * 团长的头像。
     */
    private String commanderAvatar;

    /**
     * 团长的昵称。
     */
    private String commanderNickname;

    /**
     * 开团时间。
     */
    private LocalDateTime openTime;

    /**
     * 拼团有效时间，单位为分钟，大于等于15。
     */
    private Integer effectTimeout;

    /**
     * 当前参团人数。
     */
    private Integer currentNum;

    /**
     * 成团总人数。
     */
    private Integer totalNum;

    /**
     * 店铺id。
     */
    private Long shopId;

    /**
     * 商品ID。
     */
    private Long productId;

    /**
     * 商品名称。
     */
    private String productName;

    /**
     * 商品图片。
     */
    private String productImage;

    /**
     * 购买数量。
     */
    private Integer buyNum;

    /**
     * 拼团总价。
     */
    private Long totalAmount;

    /**
     * 拼团状态，可能的值为 'ING'、'SUCCESS'、'FAIL'。
     */
    private TeamOrderStatus status;

}
