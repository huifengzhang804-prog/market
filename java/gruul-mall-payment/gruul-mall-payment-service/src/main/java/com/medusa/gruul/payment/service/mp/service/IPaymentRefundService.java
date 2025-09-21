package com.medusa.gruul.payment.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;

/**
 * @author xiaoq
 * @Description
 * @date 2022-08-08 11:13
 */
public interface IPaymentRefundService extends IService<PaymentRefund> {


    /**
     * 余额退款处理
     *
     * @param refundRequest 退款请求DTO
     * @param notifyParam   支付回调消息
     * @param refundNo      退款单号
     */
    void balanceRefundOrder(RefundRequestDTO refundRequest, String notifyParam, String refundNo);


    /**
     * 退款成功的处理
     *
     * @param async    同步/异步
     * @param refundNo 退款单号
     * @param msg      退款消息/同步退款、异步退款参数
     */
    void refundSuccess(boolean async, String refundNo, Object msg);

}
