package com.medusa.gruul.payment.service.strategy.refund.notify;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.service.common.constant.AliConst;
import com.medusa.gruul.payment.service.common.constant.WxConst;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/12/12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RefundNotifyStrategyFactory extends AbstractStrategyFactory<PayType, PayMessage, Void> {

    private final IPaymentRefundService paymentRefundService;

    @Override
    public Map<PayType, Supplier<? extends IStrategy<PayType, PayMessage, Void>>> getStrategyMap() {
        return Map.of(
                //微信退款回调
                PayType.WECHAT, () -> new RefundNotifyStrategy(paymentRefundService) {
                    @Override
                    protected String refundNo(Map<String, Object> message) {
                        log.debug("::::::微信退款回调::::::");
                        return (String) message.get(WxConst.Refund.OUT_REFUND_NO);
                    }
                },
                //支付宝退款回调
                PayType.ALIPAY, () -> new RefundNotifyStrategy(paymentRefundService) {
                    @Override
                    protected String refundNo(Map<String, Object> message) {
                        log.debug("::::::支付宝退款回调::::::");
                        return (String) message.get(AliConst.Refund.OUT_BIZ_NO);
                    }
                }
        );
    }
}
