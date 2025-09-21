package com.medusa.gruul.addon.bargain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 发起砍价商品状态
 *
 * @author wudi
 */
@Getter
@RequiredArgsConstructor
public enum BargainSponsorSkuStatus {


    /**
     * 已成功
     */
    SUCCESSFUL_BARGAIN(0, "已成功: 您的好友砍价成功（已下单"),


    /**
     * 已帮砍
     */
    HELPED(1, "已帮砍：您已帮好友砍过价"),

    /**
     * 已售罄
     */
    SOLD_OUT(2, "已售罄：您来晚一步: 活动商品已售罄"),

    /**
     * 已结束
     */
    END(3, "已结束：您来晚一步: 活动已结束"),

    /**
     * 已失败
     */
    FAILED_TO_BARGAIN(4, "已失败：可惜了，商品砍价失败"),

    /**
     * 进行中
     */
    IN_PROGRESS(5, "进行中：您的好友正在砍价中"),


    /**
     * 已到底价
     */
    RESERVE_PRICE(6, "当前商品已到底价");


    /**
     * 值
     */
    private final Integer value;

    /**
     * 描述
     */
    private final String desc;


}
