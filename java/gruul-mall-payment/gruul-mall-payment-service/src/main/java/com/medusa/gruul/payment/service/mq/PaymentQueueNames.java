package com.medusa.gruul.payment.service.mq;

/**
 * @author xiaoq
 * @Description 支付队列名称
 * @date 2022-10-08 14:44
 */
public interface PaymentQueueNames {
    /**
     * 用户余额发生改动,生成余额明细
     */
    String PAYMENT_CHANGE_CREATE_DETAIL_QUEUE = "payment.change.create.detail.queue";

}
//