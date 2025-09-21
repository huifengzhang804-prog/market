package com.medusa.gruul.addon.coupon.model;

/**
 * @author 张治保
 * date 2022/11/2
 */
public interface CouponErrorCode {


    /**
     * 优惠券已生效 禁用常规编辑
     */
    int COUPON_NEED_VALID_EDIT = 33301;

    /**
     * 优惠券为生效 应使用常规编辑
     */
    int COUPON_NEED_INVALID_EDIT = 33301;

    /**
     * 优惠券已失效
     */
    int COUPON_INVALID = 33302;

    /**
     * 优惠券库存不足
     */
    int COUPON_OUT_STOCK = 33303;

    /**
     * 超过了每人限领数量
     */
    int COUPON_OUT_LIMIT = 33304;

    /**
     * 优惠券不存在
     */
    int COUPON_NOT_EXISTS = 33305;

    /**
     * 优惠券未生效
     */
    int COUPON_NOT_WORKING = 33306;

    /**
     * 优惠券已被使用
     */
    int COUPON_USED = 33307;

    /**
     * 没有满足优惠券的使用条件
     */
    int COUPON_WRONG_USE_CONDITION = 33308;

    /**
     * 当前优惠券正在被使用
     */
    int COUPON_IS_BEING_USED = 33309;

}
