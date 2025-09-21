package com.medusa.gruul.addon.bargain.mq;

/**
 * 砍价活动队列
 *
 * @author wudi
 */
public interface BargainQueueNames {


    /**
     * 发起砍价
     */
    String BARGAIN_START = "bargain.start";

    /**
     * 砍价订单已支付
     */
    String BARGAIN_ORDER_PAID = "bargain.payment.info";

    /**
     * 砍价活动支付退款信息
     */
    String BARGAIN_REFUND_INFO = "bargain.refund.info";

    /**
     * 砍价订单关闭
     */
    String BARGAIN_ORDER_CREATE_FAIL = "bargain.order.close";
}
