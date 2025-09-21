package com.medusa.gruul.payment.service.strategy.refund.request;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.service.common.model.RefundRequestParam;
import com.medusa.gruul.payment.service.mp.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/12/12
 */
@Component
@RequiredArgsConstructor
public class RefundRequestStrategyFactory extends AbstractStrategyFactory<PayType, RefundRequestParam, Void> {
    private final IPaymentRefundService paymentRefundService;
    private final PayServiceManager payServiceManager;

    @Override
    public Map<PayType, Supplier<? extends IStrategy<PayType, RefundRequestParam, Void>>> getStrategyMap() {
        return Map.of(
                PayType.WECHAT, () -> new WechatRefundRequestStrategy(paymentRefundService, payServiceManager),
                PayType.ALIPAY, () -> new AlipayRefundRequestStrategy(paymentRefundService, payServiceManager),
                PayType.BALANCE, () -> new BalanceRefundRequestStrategy(paymentRefundService, payServiceManager)
        );
    }
}
