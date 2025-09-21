package com.medusa.gruul.payment.service.strategy.refund.notify;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 退款回调执行策略抽象
 *
 * @author xiaoq
 * @since 2022-08-08 15:31
 */
@Slf4j
@RequiredArgsConstructor
public abstract class RefundNotifyStrategy implements IStrategy<PayType, PayMessage, Void> {

    private final IPaymentRefundService paymentRefundService;

    @Override
    public Void execute(PayType type, PayMessage message) {
        String refundNo = refundNo(message.getPayMessage());
        log.debug("::退款回调::RefundNo::{}", refundNo);
        paymentRefundService.refundSuccess(true, refundNo, message);
        return VOID;
    }

    /**
     * 获取退款单号
     *
     * @param message 回调消息
     * @return 退款单号
     */
    protected abstract String refundNo(Map<String, Object> message);


}
