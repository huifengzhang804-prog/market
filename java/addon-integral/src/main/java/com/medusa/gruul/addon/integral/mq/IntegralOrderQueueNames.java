package com.medusa.gruul.addon.integral.mq;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 14:22
 **/
public interface IntegralOrderQueueNames {


    /**
     * 创建订单 后 把订单数据刷到数据库
     */
    String INTEGRAL_ORDER_CREATE_FLUSH_DATA2DB_QUEUE = "integral.order.create.flush.data2db";


    /**
     * 超时未支付 系统自动关闭订单队列
     */
    String INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE = "integral.order.auto.paidTimeout.close";


    /**
     * 积分订单支付 成功队列名
     */
    String INTEGRAL_ORDER_PAID_CALLBACK_QUEUE = "integral.order.paid.callback";

    /**
     * 积分订单发货自动完成
     */
    String INTEGRAL_ORDER_SEND_COMPLETION = "integral.order.send.completion";

    /**
     * 用户订单完成队列
     */
    String INTEGRAL_USER_ORDER_FINISH_BEHAVIOR = "integral.user.order.finish.behavior";
}
