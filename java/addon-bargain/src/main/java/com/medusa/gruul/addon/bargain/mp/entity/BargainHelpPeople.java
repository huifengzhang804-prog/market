package com.medusa.gruul.addon.bargain.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 帮砍表
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_bargain_help_people")
public class BargainHelpPeople extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickName;

    /**
     * 发起人id
     */
    private Long sponsorId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 帮砍金额
     */
    private Long helpCutAmount;

    /**
     * 砍价订单id
     */
    private Long bargainOrderId;

    /**
     * 帮砍时间
     */
    private LocalDateTime helpCutTime;
}
