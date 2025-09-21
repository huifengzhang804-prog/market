package com.medusa.gruul.order.service.modules.deliver.strategy;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.modules.deliver.model.DeliveryParam;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.mp.service.IShopOrderPackageService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.medusa.gruul.order.service.modules.deliver.strategy.DeliveryStrategyFactory.getDeliverModeShopId;

/**
 * 打印物流发货
 *
 * @author 张治保
 * @since 2024/11/2
 */
@RequiredArgsConstructor
public class ExpressPrintDeliveryStrategy implements IStrategy<DeliverType, DeliveryParam, ShopOrderPackage> {

    private final DeliverService deliverService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    @Override
    public ShopOrderPackage execute(DeliverType type, DeliveryParam param) {
        DeliveryQueryBO deliveryMatch = param.getDeliveryMatch();
        OrderDeliveryDTO orderDelivery = param.getOrderDelivery();
        UserAddressDTO receiver = orderDelivery.getReceiver();
        String expressCompanyCode = orderDelivery.getExpressCompany().getExpressCompanyCode();
        ShopOrderPackage shopOrderPackage = new ShopOrderPackage()
                .setShopId(deliveryMatch.getShopId())
                .setOrderNo(deliveryMatch.getOrderNo())
                .setStatus(PackageStatus.WAITING_FOR_RECEIVE)
                .setType(DeliverType.PRINT_EXPRESS)
                .setExpressCompanyName(orderDelivery.getExpressCompany().getExpressCompanyName())
                .setExpressCompanyCode(expressCompanyCode)
                .setReceiverName(receiver.getName())
                .setReceiverMobile(receiver.getMobile())
                .setReceiverAddress(receiver.getAddress())
                .setRemark(orderDelivery.getRemark())
                .setDeliveryTime(LocalDateTime.now())
                .setDeliverShopId(getDeliverModeShopId(deliveryMatch))
                .setSelfShopType(orderDelivery.getSelfShopType());
        shopOrderPackage.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(shopOrderPackage).longValue());
        //拆分发货
        Long packageId = shopOrderPackage.getId();
        BigDecimal weight = deliverService.splitShopOrderItems(
                shopOrderPackage,
                new OrderPackageKeyDTO().setOrderNo(deliveryMatch.getOrderNo())
                        .setShopOrderNo(deliveryMatch.getShopOrderNo())
                        .setShopId(deliveryMatch.getShopId())
                        .setPackageId(packageId),
                orderDelivery.getItems()
        );

        //快递运单号
        String expressNo = orderAddonDistributionSupporter.printDeliverGoods(
                new SendDeliveryDTO()
                        .setSender(orderDelivery.getSender())
                        .setReceiver(orderDelivery.getReceiver())
                        .setRemark(orderDelivery.getRemark())
                        .setDropDeliver(false)
                        .setExpressCompanyCode(expressCompanyCode)
                        .setCargo(orderDelivery.getCargo())
                        .setNo(deliveryMatch.getOrderNo())
                        .setWeight(weight)
                        .setSelfShopType(orderDelivery.getSelfShopType())
        );
        shopOrderPackage.setExpressNo(expressNo);
        boolean success = shopOrderPackageService.save(shopOrderPackage);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        return shopOrderPackage;
    }
}
