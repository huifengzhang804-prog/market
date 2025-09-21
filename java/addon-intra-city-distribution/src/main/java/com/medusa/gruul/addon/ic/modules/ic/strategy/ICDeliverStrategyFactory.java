package com.medusa.gruul.addon.ic.modules.ic.strategy;

import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopOrderDao;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 发货方式策略工厂
 *
 * @author 张治保
 * @since 2024/8/30
 */
@Component
@RequiredArgsConstructor
public class ICDeliverStrategyFactory extends AbstractStrategyFactory<ICDeliveryType, ICOrder, Void> {

    private final IUuptApiFactory uuptApiFactory;
    private final ICShopOrderDao shopOrderDao;
    private final GlobalAppProperties globalAppProperties;

    @Override
    public Map<ICDeliveryType, Supplier<? extends IStrategy<ICDeliveryType, ICOrder, Void>>> getStrategyMap() {
        return Map.of(
                ICDeliveryType.SELF, () -> new ICSelfDeliverStrategy(shopOrderDao),
                ICDeliveryType.UUPT, () -> new ICUuptDeliverStrategy(uuptApiFactory, shopOrderDao, globalAppProperties)
        );
    }
}
