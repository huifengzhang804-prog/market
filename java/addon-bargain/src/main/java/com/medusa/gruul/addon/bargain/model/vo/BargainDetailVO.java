package com.medusa.gruul.addon.bargain.model.vo;

import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.HelpCutAmountType;
import com.medusa.gruul.common.model.base.StackableDiscount;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainDetailVO {


    /**
     * 砍价活动id
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 砍价活动名称
     */
    private String name;

    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    private LocalDateTime endTime;

    /**
     * 砍价人数
     */
    private Integer bargainingPeople;

    /**
     * 砍价有效期
     */
    private Integer bargainValidityPeriod;

    /**
     * 是否自我砍价
     */
    private Boolean isSelfBargain;

//    /**
//     * 用户类型
//     */
//    private UserType userType;

    /**
     * 活动预热时间
     */
    private Integer activityPreheat;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;

    /**
     * 活动状态
     */
    private ActivityStatus status;

    /**
     * 帮砍金额
     */
    private HelpCutAmountType helpCutAmount;


    /**
     * 砍价商品
     */
    private List<BargainActivityProductVO> bargainActivityProducts;
}
