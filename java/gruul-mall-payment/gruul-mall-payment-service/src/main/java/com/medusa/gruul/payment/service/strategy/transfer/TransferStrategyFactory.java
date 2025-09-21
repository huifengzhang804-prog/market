package com.medusa.gruul.payment.service.strategy.transfer;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/12/16
 */
@Component
@RequiredArgsConstructor
public class TransferStrategyFactory extends AbstractStrategyFactory<PayType, TransferParam, TransferResult> {

    private final PayServiceManager payServiceManager;

    @Override
    public Map<PayType, Supplier<? extends IStrategy<PayType, TransferParam, TransferResult>>> getStrategyMap() {
        return Map.of(
                PayType.WECHAT, () -> new WechatTransferStrategy(payServiceManager),
                PayType.ALIPAY, () -> new AliTransferStrategy(payServiceManager)
        );
    }
}
