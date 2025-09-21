package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import com.medusa.gruul.order.service.model.dto.OrdersDeliveryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.modules.deliver.service.ShopOrderDeliverService;
import com.medusa.gruul.order.service.modules.order.service.StoreOrderService;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 门店订单服务实现层
 *
 * @author xiaoq
 * Description 门店订单服务实现层
 * date 2023-03-17 11:12
 */
@Service
@RequiredArgsConstructor
public class StoreOrderServiceImpl implements StoreOrderService {

    private final IOrderService orderService;
    private final IShopOrderItemService shopOrderItemService;
    private final DeliverService deliverService;
    private final ShopOrderDeliverService shopOrderDeliverService;
    private final OrderRabbitService orderRabbitService;


    /**
     * 批量门店订单发货
     *
     * @param orderNos 订单nos
     * @param storeId  门店id
     */
    @Override
    public void batchStoreOrderDeliver(Set<String> orderNos, Long storeId) {
        List<Order> orderList = orderService.lambdaQuery()
                .select(Order::getNo)
                .like(Order::getExtra, storeId)
                .eq(Order::getStatus, OrderStatus.PAID)
                .in(Order::getNo, orderNos)
                .list();
        if (CollUtil.isEmpty(orderList)) {
            throw OrderError.STORE_STOCK_ORDER_NOT_EXIST.exception();
        }
        Set<String> orderNosSet = orderList.stream().map(Order::getNo).collect(Collectors.toSet());
        Long shopId = ISecurity.userMust().getShopId();
        List<ShopOrderItem> items = TenantShop.disable(() ->
                shopOrderItemService.lambdaQuery()
                        .select(ShopOrderItem::getId, ShopOrderItem::getNum, ShopOrderItem::getOrderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .in(ShopOrderItem::getOrderNo, orderNosSet)
                        .list()
        );
        if (CollUtil.isEmpty(items)) {
            throw OrderError.STORE_STOCK_ORDER_NOT_EXIST.exception();
        }
        Map<String, List<OrderDeliveryItemDTO>> orderNoItemsMap = items.stream().collect(
                Collectors.groupingBy(
                        ShopOrderItem::getOrderNo,
                        Collectors.mapping(this::createOrderDeliveryItem, Collectors.toList()
                        )
                )
        );
        List<OrdersDeliveryDTO> deliveries = orderNoItemsMap.entrySet().stream()
                .map(this::createOrdersDelivery).toList();
        shopOrderDeliverService.batchDeliver(
                new DeliveryQueryBO()
                        .setShopId(shopId),
                deliveries
        );
    }

    private OrderDeliveryItemDTO createOrderDeliveryItem(ShopOrderItem item) {
        OrderDeliveryItemDTO deliveryItem = new OrderDeliveryItemDTO();
        deliveryItem.setItemId(item.getId());
        deliveryItem.setNum(item.getNum());
        return deliveryItem;
    }

    private OrdersDeliveryDTO createOrdersDelivery(Map.Entry<String, List<OrderDeliveryItemDTO>> item) {
        OrdersDeliveryDTO ordersDelivery = new OrdersDeliveryDTO();
        ordersDelivery.setOrderNo(item.getKey());
        OrderDeliveryDTO orderDelivery = new OrderDeliveryDTO();
        orderDelivery.setDeliverType(DeliverType.WITHOUT);
        orderDelivery.setRemark("门店备货完成");
        orderDelivery.setItems(item.getValue());
        ordersDelivery.setOrderDelivery(orderDelivery);
        return ordersDelivery;
    }

    @Override
    public void storeOrderConfirmPackage(OrderPackageKeyDTO orderPackageKey) {
        String storeId = orderPackageKey.getExtra().get("shopStoreId").toString();
        String orderNo = orderPackageKey.getOrderNo();
        Order order = orderService.lambdaQuery()
                .select(Order::getNo)
                .like(Order::getExtra, storeId)
                .eq(Order::getStatus, OrderStatus.PAID)
                .eq(Order::getNo, orderNo)
                .one();
        if (order == null) {
            throw OrderError.STORE_ORDER_NOT_EXIST.exception();
        }
        deliverService.packageConfirm(false, orderPackageKey, PackageStatus.BUYER_WAITING_FOR_COMMENT);
        orderRabbitService.sendPrintOrder(
                new OrderPrintDTO()
                        .setOrderNo(orderNo)
                        .setScene(PrintScene.AUTO_DELIVERY)
                        .setMode(PrintMode.STORE_PICKUP_SELF)
                        .setShopIds(Set.of(orderPackageKey.getShopId()))
        );
    }
}
