package com.medusa.gruul.payment.service.strategy.config;

import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/12/11
 */
@Component
@RequiredArgsConstructor
public class PayConfigStrategyFactory extends AbstractStrategyFactory<PayType, MerchantDetailsDTO, Void> {

    private final PaymentProperty paymentProperty;
    private final GlobalAppProperties globalAppProperties;

    @Override
    public Map<PayType, Supplier<? extends IStrategy<PayType, MerchantDetailsDTO, Void>>> getStrategyMap() {
        return Map.of(
                PayType.WECHAT, () -> new WechatConfigStrategy(paymentProperty, globalAppProperties),
                PayType.ALIPAY, () -> new AliConfigStrategy(paymentProperty, globalAppProperties)
        );
    }
}
