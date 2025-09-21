package com.medusa.gruul.addon.team.mq;

/**
 * @author 张治保
 * date 2023/3/16
 */
public interface TeamRabbitNames {

    /**
     * 订单支付成功队列
     */
    String ORDER_PAID = "team.order.paid";

    /**
     * 订单关闭
     */
    String ORDER_CLOSE = "team.order.close";
}
