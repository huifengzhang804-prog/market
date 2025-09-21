package com.medusa.gruul.addon.rebate.mq;

public interface RebateRabbitQueueNames {


    /**
     * 创建订单队列
     */
    String REBATE_ORDER_CREATED = "rebate.create.order";

    /**
     * 订单支付队列
     */
    String REBATE_ORDER_PAYED = "rebate.order.paid";


    /**
     * 订单已关闭
     */
    String REBATE_ORDER_CLOSED = "rebate.order.closed";

    /**
     * 订单完成
     */
    String REBATE_ORDER_ACCOMPLISH = "rebate.order.accomplish";

    /**
     * 提现工单审核未通过
     */
    String WITHDRAW_ORDER_FORBIDDEN = "rebate.withdraw.order.forbidden";
}
