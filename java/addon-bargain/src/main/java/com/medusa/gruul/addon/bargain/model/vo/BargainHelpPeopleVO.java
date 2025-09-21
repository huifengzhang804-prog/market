package com.medusa.gruul.addon.bargain.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainHelpPeopleVO {


    /**
     * 帮砍人id
     */
    private Long userId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 砍价订单id
     */
    private Long bargainOrderId;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickName;

    /**
     * 帮砍金额
     */
    private Long helpCutAmount;

    /**
     * 帮砍时间
     */
    private LocalDateTime helpCutTime;

}