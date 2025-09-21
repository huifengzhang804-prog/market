package com.medusa.gruul.order.service.modules.deliver.strategy;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.modules.deliver.model.DeliveryParam;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mp.service.IShopOrderPackageService;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/11/2
 */
@Component
@RequiredArgsConstructor
public class DeliveryStrategyFactory extends AbstractStrategyFactory<DeliverType, DeliveryParam, ShopOrderPackage> {

    private final DeliverService deliverService;
    private final IShopOrderService shopOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IShopOrderItemService shopOrderItemService;
    private final OrderAfsService orderAfsService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    /**
     * 获取发货方
     *
     * @param deliveryMatch 发货查询DTO
     * @return 发货方店铺、供应商id
     */
    public static Long getDeliverModeShopId(DeliveryQueryBO deliveryMatch) {
        if (ShopDeliverModeSettingsEnum.PLATFORM == (deliveryMatch.getShopDeliverModeSettings())) {
            return (long) CommonPool.NUMBER_ZERO;
        }
        if (deliveryMatch.getSupplierId() != null) {
            return deliveryMatch.getSupplierId();
        }
        return deliveryMatch.getShopId();
    }

    @Override
    public Map<DeliverType, Supplier<? extends IStrategy<DeliverType, DeliveryParam, ShopOrderPackage>>> getStrategyMap() {
        return Map.of(
                DeliverType.WITHOUT, () -> new WithoutDeliveryStrategy(deliverService, shopOrderPackageService),
                DeliverType.EXPRESS, () -> new ExpressDeliveryStrategy(deliverService, shopOrderPackageService),
                DeliverType.PRINT_EXPRESS, () -> new ExpressPrintDeliveryStrategy(deliverService, shopOrderPackageService, orderAddonDistributionSupporter),
                DeliverType.IC_MERCHANT, () -> new ICMerchantDeliveryStrategy(shopOrderService, shopOrderItemService, shopOrderPackageService, orderAddonDistributionSupporter, orderRabbitService, orderAfsService),
                DeliverType.IC_OPEN, () -> new ICOpenDeliveryStrategy(shopOrderService, shopOrderItemService, shopOrderPackageService, orderAddonDistributionSupporter, orderRabbitService, orderAfsService)
        );
    }
}
