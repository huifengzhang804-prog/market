package com.medusa.gruul.payment.service.strategy.pay;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
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
public class PayRequestStrategyFactory extends AbstractStrategyFactory<Platform, PaymentInfoDTO, PayResult> {
    private final MultiPayOrderService multiPayOrderService;
    private final PayServiceManager payServiceManager;

    @Override
    public Map<Platform, Supplier<? extends IStrategy<Platform, PaymentInfoDTO, PayResult>>> getStrategyMap() {
        //app 支付
        IStrategy<Platform, PaymentInfoDTO, PayResult> app = new AppPayRequestStrategy(multiPayOrderService, payServiceManager);
        Supplier<? extends IStrategy<Platform, PaymentInfoDTO, PayResult>> appSupplier = () -> app;
        //微信 jsapi 支付
        IStrategy<Platform, PaymentInfoDTO, PayResult> wechatJsApi = new WechatJsApiRequestStrategy(multiPayOrderService, payServiceManager);
        Supplier<? extends IStrategy<Platform, PaymentInfoDTO, PayResult>> wechatJsApiSupplier = () -> wechatJsApi;
        return Map.of(
                Platform.ANDROID, appSupplier,
                Platform.IOS, appSupplier,
                Platform.HARMONY, appSupplier,
                Platform.H5, () -> new H5PayRequestStrategy(multiPayOrderService, payServiceManager),
                Platform.PC, () -> new PcPayRequestStrategy(multiPayOrderService, payServiceManager),
                Platform.WECHAT_MINI_APP, wechatJsApiSupplier,
                Platform.WECHAT_MP, wechatJsApiSupplier
        );
    }
}
