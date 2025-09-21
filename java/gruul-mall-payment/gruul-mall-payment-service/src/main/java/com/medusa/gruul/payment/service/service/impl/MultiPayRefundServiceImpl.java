package com.medusa.gruul.payment.service.service.impl;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.service.service.MultiPayRefundService;
import com.medusa.gruul.payment.service.strategy.refund.notify.RefundNotifyStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 退款回调Impl
 *
 * @author xiaoq
 * @Description MultiPayRefundServiceImpl.java
 * @date 2022-08-08 10:07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiPayRefundServiceImpl implements MultiPayRefundService {


    private final RefundNotifyStrategyFactory refundNotifyStrategyFactory;

    /**
     * 退款回调处理
     *
     * @param payType    支付渠道枚举
     * @param payMessage 退款消息
     */
    @Override
    public void refundNotify(PayType payType, PayMessage payMessage) {
        refundNotifyStrategyFactory.exec(payType, payMessage);
    }


}
