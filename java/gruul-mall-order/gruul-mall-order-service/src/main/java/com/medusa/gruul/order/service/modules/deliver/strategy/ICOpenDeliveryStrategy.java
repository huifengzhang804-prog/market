package com.medusa.gruul.order.service.modules.deliver.strategy;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.modules.deliver.model.DeliveryParam;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mp.service.IShopOrderPackageService;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.medusa.gruul.order.service.modules.deliver.strategy.DeliveryStrategyFactory.getDeliverModeShopId;

/**
 * @author 张治保
 * @since 2024/11/2
 */
@RequiredArgsConstructor
public class ICOpenDeliveryStrategy implements IStrategy<DeliverType, DeliveryParam, ShopOrderPackage> {

    private final IShopOrderService shopOrderService;
    private final IShopOrderItemService shopOrderItemService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;
    private final OrderRabbitService orderRabbitService;
    private final OrderAfsService orderAfsService;

    @Override
    public ShopOrderPackage execute(DeliverType type, DeliveryParam param) {
        DeliveryQueryBO deliveryMatch = param.getDeliveryMatch();
        OrderDeliveryDTO orderDelivery = param.getOrderDelivery();
        UserAddressDTO receiver = orderDelivery.getReceiver();
        String orderNo = deliveryMatch.getOrderNo();
        Long shopId = deliveryMatch.getShopId();
        //查询该订单售后状态信息
        List<ShopOrderItem> shopOrderItem = TenantShop.disable(
                () -> shopOrderItemService.lambdaQuery()
                        .select(ShopOrderItem::getAfsNo, ShopOrderItem::getAfsStatus)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .orderByAsc(ShopOrderItem::getProductName)
                        .list()
        );
        //检查是否有不可关闭的售后
        for (ShopOrderItem item : shopOrderItem) {
            if (StrUtil.isNotBlank(item.getAfsNo()) && !item.getAfsStatus().isCanChangePackageStatus()) {
                throw OrderError.AFS_CANNOT_CLOSE.exception();
            }
        }
        //生成店铺发货包裹
        ShopOrderPackage shopOrderPackage = new ShopOrderPackage()
                .setShopId(shopId)
                .setOrderNo(orderNo)
                .setStatus(PackageStatus.WAITING_FOR_RECEIVE)
                .setType(DeliverType.IC_OPEN)
                .setReceiverName(receiver.getName())
                .setReceiverMobile(receiver.getMobile())
                .setReceiverAddress(receiver.fullAddress())
                .setRemark(orderDelivery.getRemark())
                .setDeliveryTime(LocalDateTime.now())
                .setDeliverShopId(getDeliverModeShopId(deliveryMatch))
                .setSelfShopType(orderDelivery.getSelfShopType());
        boolean success = shopOrderPackageService.save(shopOrderPackage);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        //更新发货状态
        success = TenantShop.disable(
                () -> shopOrderItemService.lambdaUpdate()
                        .set(ShopOrderItem::getPackageId, shopOrderPackage.getId())
                        .set(ShopOrderItem::getAfsStatus, AfsStatus.NONE)
                        .set(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                        .set(ShopOrderItem::getUpdateTime, LocalDateTime.now())
                        .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .isNull(ShopOrderItem::getPackageId)
                        .update()
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        //查询收货人信息
        ShopOrder shopOrder = shopOrderService.lambdaQuery()
                .select(ShopOrder::getRemark, ShopOrder::getExtra)
                .eq(ShopOrder::getShopId, shopId)
                .eq(ShopOrder::getOrderNo, orderNo)
                .one();
        orderAddonDistributionSupporter.icOrderReport(
                false,
                new ICOrder()
                        .setShopId(shopId)
                        .setOrderNo(orderNo)
                        .setPickupCode(shopOrder.getExtra().getPickupCode())
                        .setRemark(shopOrder.getRemark())
        );
        orderRabbitService.sendPrintOrder(
                new OrderPrintDTO()
                        .setOrderNo(orderNo)
                        .setScene(PrintScene.AUTO_DELIVERY)
                        .setMode(PrintMode.INTRA_CITY)
                        .setShopIds(Set.of(shopId))
        );
        //尝试关闭售后 如果可关闭
        orderAfsService.closeAfsIfClosable(Boolean.TRUE, shopOrderItem, "商家发货，自动关闭售后");
        return shopOrderPackage.setSendDelayConfirm(false);
    }
}
