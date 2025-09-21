package com.medusa.gruul.addon.bargain.model.vo;

import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
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
public class BargainOrderVO {

    /**
     * id
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 发起人id
     */
    private Long sponsorId;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 砍价状态
     */
    private BargainStatus bargainStatus;

    /**
     * 底价
     */
    private Long floorPrice;

    /**
     * 砍价人数
     */
    private Integer bargainingPeople;

    /**
     * 发布砍价时间
     */
    private LocalDateTime publishBargainingTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 订单号
     */
    private String orderNo;
}
