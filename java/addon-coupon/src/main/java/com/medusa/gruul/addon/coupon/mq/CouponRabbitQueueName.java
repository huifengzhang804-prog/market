package com.medusa.gruul.addon.coupon.mq;

/**
 * @author 张治保
 * date 2022/11/11
 */
public interface CouponRabbitQueueName {


    /**
     * 订单关闭归还优惠券 队列
     */
    String COUPON_ORDER_CLOSED = "coupon.order.closed";
}
