package com.medusa.gruul.payment.service.config;

import com.egzosn.pay.spring.boot.autoconfigue.PayAutoConfiguration;
import com.egzosn.pay.spring.boot.core.PayServiceConfigurer;
import com.egzosn.pay.spring.boot.core.builders.MerchantDetailsServiceBuilder;
import com.egzosn.pay.spring.boot.core.configurers.MerchantDetailsServiceConfigurer;
import com.egzosn.pay.spring.boot.core.configurers.PayMessageConfigurer;
import com.egzosn.pay.spring.boot.core.merchant.MerchantDetailsService;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.AliPaymentPlatform;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.PaymentPlatforms;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxV3PaymentPlatform;
import com.medusa.gruul.overview.api.rpc.WithdrawRpcService;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.service.MultiPayRefundService;
import com.medusa.gruul.payment.service.strategy.notify.AliPayMessageHandler;
import com.medusa.gruul.payment.service.strategy.notify.WxPayMessageHandler;
import com.medusa.gruul.payment.service.strategy.verify.PayStatusVerifyStrategyFactory;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 支付服务配置
 *
 * @author xiaoq
 * @see PayAutoConfiguration
 * @since 2022-07-12 16:47
 */
@Configuration
@RequiredArgsConstructor
public class MerchantPayServiceConfig implements PayServiceConfigurer {

    private final PayStatusVerifyStrategyFactory payStatusVerifyStrategyFactory;
    private final MultiPayOrderService orderPaymentService;
    private final MultiPayRefundService multiPayRefundService;
    @Resource
    @Lazy
    private WithdrawRpcService withdrawRpcService;


    @Override
    public void configure(MerchantDetailsServiceConfigurer configurer) {
        //使用redis 缓存配置
        configurer.initBuilder(new MerchantDetailsServiceBuilder() {
            @Override
            protected MerchantDetailsService<?> performBuild() {
                return RedisMerchantPayConfigManager.instance;
            }
        });
    }

    @Override
    public void configure(PayMessageConfigurer configurer) {
        RedisMerchantPayConfigManager.instance.setPayMessageConfigurer(configurer);
        configurer.addHandler(
                PaymentPlatforms.getPaymentPlatform(AliPaymentPlatform.PLATFORM_NAME),
                new AliPayMessageHandler(payStatusVerifyStrategyFactory, orderPaymentService, multiPayRefundService)
        );
        configurer.addHandler(
                PaymentPlatforms.getPaymentPlatform(WxV3PaymentPlatform.PLATFORM_NAME),
                new WxPayMessageHandler(payStatusVerifyStrategyFactory, orderPaymentService, multiPayRefundService, withdrawRpcService)
        );
    }
}
