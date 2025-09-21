package com.medusa.gruul.addon.bargain.model;

/**
 * 砍价错误码
 * @author wudi
 */
public interface BargainErrorCode {


    /**
     * 砍价商品在相同时间段内已存在
     */
    int BARGAIN_PRODUCT_EXISTS = 79111;

    /**
     * 砍价活动不存在
     */
    int BARGAIN_ACTIVITY_NOT_EXISTS = 79112;

    /**
     * 当前商品不是砍价商品
     */
    int BARGAIN_PRODUCT_NOT_EXISTS = 79113;

    /**
     * 砍价失败
     */
    int BARGAIN_FAILED = 79114;


    /**
     * 砍价活动未开始
     */
    int BARGAIN_ACTIVITY_NOT_START = 79115;

    /**
     * 您已帮忙砍过价
     */
    int BARGAIN_HELPED = 79116;

    /**
     * 您已发起砍价
     */
    int BARGAIN_INITIATED = 79117;


    /**
     * 砍价支付成功
     */
    int BARGAIN_SUCCESS = 79118;


}
