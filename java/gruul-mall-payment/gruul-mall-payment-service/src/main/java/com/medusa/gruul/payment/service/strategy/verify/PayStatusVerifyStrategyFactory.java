package com.medusa.gruul.payment.service.strategy.verify;

import cn.hutool.extra.spring.SpringUtil;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.service.service.MultiPayOrderService;
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
public class PayStatusVerifyStrategyFactory extends AbstractStrategyFactory<PayType, String, TradeStatus> {

    private final MultiPayOrderService multiPayOrderService;
    private volatile PayServiceManager payServiceManager;

    @Override
    public Map<PayType, Supplier<? extends IStrategy<PayType, String, TradeStatus>>> getStrategyMap() {
        setPayServiceManager();
        return Map.of(
                PayType.WECHAT, () -> new WechatPayStatusVerifyStrategy(
                        multiPayOrderService, payServiceManager
                ),
                PayType.ALIPAY, () -> new AliPayStatusVerifyStrategy(
                        multiPayOrderService, payServiceManager
                )
        );
    }

    private void setPayServiceManager() {
        if (payServiceManager == null) {
            synchronized (this) {
                if (payServiceManager == null) {
                    payServiceManager = SpringUtil.getBean(PayServiceManager.class);
                }
            }
        }
    }
}
