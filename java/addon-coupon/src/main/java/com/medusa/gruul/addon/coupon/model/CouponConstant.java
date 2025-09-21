package com.medusa.gruul.addon.coupon.model;

/**
 * @author 张治保
 * date 2022/11/2
 */
public interface CouponConstant {

    /**
     * 优惠券库存字段名
     */
    String COUPON_STOCK_FIELD = "stock";

    /**
     * 店铺优惠券缓存key
     */
    String COUPON_CACHE_KEY = "addon:coupon:cache";

    /**
     * 用户优惠券缓存key
     */
    String COUPON_USER_CACHE_KEY = "addon:coupon:cache";

    /**
     * 优惠券编辑分布式锁
     */
    String COUPON_EDIT_LOCK_KEY = "addon:coupon:edit:lock";
    
    /**
     * 用户领取优惠券分布式锁
     */
    String COUPON_USER_COLLECT_LOCK = "addon:coupon:user:collect:lock";

    /**
     * 用户使用优惠券分布式锁
     */
    String COUPON_USER_USE_LOCK = "addon:coupon:user:use:lock";
}
