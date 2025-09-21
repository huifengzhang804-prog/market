package com.medusa.gruul.payment.service.service;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.payment.api.enums.NotifyStatus;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.service.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.common.model.PayRequestOrder;

/**
 *
 *      商户业务订单支付接口
 *
 *
 * @author xiaoq
 * @ Description  MultiPayOrderService.java
 * @date 2022-07-25 15:57
 */
public interface MultiPayOrderService {
    /**
     * 业务订单预处理
     *
     * @param paymentInfoDTO  支付信息DTO
     * @return  PayRequestOrder.java
     */
    PayRequestOrder pretreatmentOrder(PaymentInfoDTO paymentInfoDTO);

    /**
     * 支付成功处理
     * @param payMessage 支付回调消息
     */
    void success(PayMessage payMessage);

    /**
     * 获取支付记录
     *
     * @param orderNum 内部业务订单号
     * @return 业务订单支付记录
     */
    OrderPaymentRecord paymentRecord(String orderNum);


    /**
     * 获取业务订单支付状态 by outTradeNo
     *
     * @param outTradeNo 业务订单号
     * @return 处理状态
     */
    NotifyStatus orderPayStatus(String outTradeNo);


    /**
     *  余额支付
     * @param paymentInfoDTO 支付信息DTO
     */
    void balancePay(PaymentInfoDTO paymentInfoDTO);
}
