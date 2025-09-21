package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.*;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.order.service.model.bo.OrderPage;
import com.medusa.gruul.order.service.model.bo.ShopOrderQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderPlatFormDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderQueryStatus;
import com.medusa.gruul.order.service.model.vo.OrderPlatFormDeliveryVO;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderCallShopRpcService;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.service.PrintTaskService;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单查询服务实现类
 * 依赖循环问题，把用到shopRpc的方法单独放到一个新类里面
 *
 * @author 张治保
 * date 2022/6/16
 */
@Service
@RequiredArgsConstructor
public class QueryOrderCallShopRpcServiceImpl implements QueryOrderCallShopRpcService {

    private final IOrderService orderService;
    private final ShopRpcService shopRpcService;
    private final PrintTaskService printTaskService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    @Override
    public IPage<Order> orderPage(boolean export, OrderQueryDTO queryPage) {
        IPage<Order> page = TenantShop.disable(() -> orderService.orderPage(queryPage));
        OrderPage orderPage = new OrderPage(page);
        List<Order> orders = orderPage.getRecords();
        if (CollUtil.isEmpty(orders)) {
            return orderPage;
        }

        List<ShopOrder> shopOrders = TenantShop.disable(
                () -> orderService.getShopOrders(
                        new ShopOrderQueryBO()
                                .setStatus(queryPage.getStatus())
                                .setBuyerId(queryPage.getBuyerId())
                                .setProductName(queryPage.getProductName())
                                .setReceiverName(queryPage.getReceiverName())
                                .setShopIds(queryPage.getShopIds())
                                .setSupplierId(queryPage.getSupplierId())
                                .setSellTypes(queryPage.getSellType() == null ? null : Set.of(queryPage.getSellType()))
                                .setOrderNos(
                                        orders.stream().map(Order::getNo).collect(Collectors.toSet())
                                )
                )
        );
        if (CollUtil.isEmpty(shopOrders)) {
            return orderPage;
        }
        //平台、店铺、供应商订单查询，需要店铺类型和店铺模式
        ISecurity.match()
                .when(
                        secureUser -> {
                            // 初始化集合和映射
                            Set<Long> shopIds = shopOrders.stream()
                                    .map(ShopOrder::getShopId)
                                    .collect(Collectors.toSet());
                            Set<Long> supplierIds = shopOrders.stream()
                                    .flatMap(order -> order.getShopOrderItems().stream().map(ShopOrderItem::getSupplierId))
                                    .filter(Objects::nonNull) // 过滤掉 null 值
                                    .collect(Collectors.toSet());

                            List<ShopInfoVO> shopInfoVOList = shopRpcService.getShopInfoByShopIdList(shopIds);
                            Map<Long, ShopInfoVO> shopInfoById = shopInfoVOList.stream()
                                    .collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));

                            if (!supplierIds.isEmpty()) {
                                List<ShopInfoVO> supplierInfoVOList = shopRpcService.getShopInfoByShopIdList(supplierIds);
                                Map<Long, ShopInfoVO> supplierInfoById = supplierInfoVOList.stream()
                                        .collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));
                                shopInfoById.putAll(supplierInfoById); // 将供应商店铺信息合并到主店铺信息映射中
                            }

                            // 设置店铺类型和模式
                            shopOrders.forEach(shopOrder -> {
                                ShopInfoVO shopInfo = shopInfoById.get(shopOrder.getShopId());
                                if (shopInfo != null) {
                                    shopOrder.setShopType(shopInfo.getShopType());
                                    shopOrder.setShopMode(shopInfo.getShopMode());
                                }

                                // 设置代销商品供应商的店铺类型
                                shopOrder.setShopOrderItems(
                                        shopOrder.getShopOrderItems().stream()
                                                .peek(item -> {
                                                    Long supplierId = item.getSupplierId();
                                                    if (supplierId != null) {
                                                        ShopInfoVO supplierInfo = shopInfoById.get(supplierId);
                                                        if (supplierInfo != null) {
                                                            item.setSupplierShopType(supplierInfo.getShopType());
                                                        }
                                                    }
                                                })
                                                .collect(Collectors.toList())
                                );
                            });
                        },
                        Roles.SUPPLIER_ADMIN, Roles.SUPER_ADMIN, Roles.ADMIN, Roles.SUPER_CUSTOM_ADMIN,Roles.USER
                );

        //根据订单号分组 获取每个订单下的店铺订单
        Map<String, List<ShopOrder>> groupedShopOrders = shopOrders.stream().collect(Collectors.groupingBy(ShopOrder::getOrderNo));
        //查询同城订单配送状态
        Set<String> icOrderNos = orders.stream()
                .filter(order -> DistributionMode.INTRA_CITY_DISTRIBUTION == order.getDistributionMode())
                .map(Order::getNo)
                .collect(Collectors.toSet());
        Map<String, ICStatus> icOrderStatusMap = CollUtil.isEmpty(icOrderNos) ? Map.of() : MapUtil.emptyIfNull(orderAddonDistributionSupporter.icOrderStatus(icOrderNos, Boolean.TRUE));

        Map<PrintMode, Set<PrintLink>> printLinkMap = export || !ISecurity.anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN) ? Map.of() : printTaskService.printLinks(queryPage.getShopIds().iterator().next());
        //查询同城打印联
        orders.forEach(order -> {
            String orderNo = order.getNo();
            order.setShopOrders(groupedShopOrders.get(orderNo));
            //同城配送状态
            ICStatus icStatus = icOrderStatusMap.get(orderNo);
            if (icStatus != null) {
                order.setIcStatus(icStatus.getStatus());
                order.setIcStatusDesc(icStatus.getStatusDesc());
            }
            //打印联
            // 订单已关闭，不渲染打印联数据
            if (orderClosed(order)) {
                return;
            }
            DistributionMode distributionMode = order.getDistributionMode();
            if (DistributionMode.INTRA_CITY_DISTRIBUTION == distributionMode
                    || DistributionMode.SHOP_STORE == distributionMode
            ) {
                PrintMode printMode = DistributionMode.INTRA_CITY_DISTRIBUTION == distributionMode ? PrintMode.INTRA_CITY : PrintMode.STORE_PICKUP_SELF;
                order.setPrintLinks(printLinkMap.get(printMode));
            }
        });
        return orderPage;
    }

    /**
     * 判断订单是否已关闭
     *
     * @param order 订单
     * @return 是否已关闭
     */
    private boolean orderClosed(Order order) {
        OrderStatus status = order.getStatus();
        if (status == null || status.isClosed()) {
            return true;
        }
        List<ShopOrder> shopOrders = order.getShopOrders();
        if (CollUtil.isEmpty(shopOrders)) {
            return true;
        }
        for (ShopOrder shopOrder : shopOrders) {
            ShopOrderStatus shopOrderStatus = shopOrder.getStatus();
            if (shopOrderStatus == null || shopOrderStatus.isClosed()) {
                continue;
            }
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItems();
            if (CollUtil.isEmpty(shopOrderItems)) {
                continue;
            }
            for (ShopOrderItem shopOrderItem : shopOrderItems) {
                if (ItemStatus.OK == shopOrderItem.getStatus()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    @Override
    public OrderPlatFormDeliveryVO getPlatFormDeliveryOrder(OrderPlatFormDeliveryDTO dto) {
        //订单信息
        List<Order> orders = TenantShop.disable(() -> orderService.getPlatFormDeliveryOrders(dto));
        if (CollUtil.isEmpty(orders)) {
            return null;
        }
        //订单店铺商品信息
        dto.setOrderNos(orders.stream().map(Order::getNo).collect(Collectors.toSet()));

        //供应商订单
        List<Order> supplierOrders = orders.stream().map(Order::copyOf).collect(Collectors.toList());
        //商品为采购和自有的订单
        List<ShopOrder> shopOrderList = TenantShop.disable(
                () -> orderService.getShopOrders(
                        new ShopOrderQueryBO()
                                .setStatus(OrderQueryStatus.UN_DELIVERY)
                                .setSellTypes(Set.of(SellType.PURCHASE, SellType.OWN))
                                .setOrderNos(
                                        dto.getOrderNos()
                                )
                )
        );
        if (CollUtil.isNotEmpty(shopOrderList)) {
            //自营店铺
            List<ShopOrderItem> shopOrderItemList = shopOrderList.stream()
                    .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                    .collect(Collectors.toList());
            //自营店铺类型 shopType 1 自营
            Map<Long, ShopType> shopTypeMap = getShopTypeMap(shopOrderItemList, SelfShopTypeEnum.SELF_SHOP);
            //筛选自营店铺商品
            for (ShopOrder order : shopOrderList) {
                List<ShopOrderItem> shopOrderItems = order.getShopOrderItems();
                order.setShopOrderItems(shopOrderItems.stream().filter(item -> {
                    ShopType type = shopTypeMap.getOrDefault(item.getShopId(), ShopType.ORDINARY);
                    return type.equals(ShopType.SELF_OWNED);
                }).collect(Collectors.toList()));
            }
            if (CollUtil.isNotEmpty(shopOrderList)) {
                Map<String, List<ShopOrder>> groupedShopOrders = shopOrderList.stream().collect(Collectors.groupingBy(ShopOrder::getOrderNo));
                orders.forEach(
                        order ->
                                order.setShopOrders(groupedShopOrders.get(order.getNo()))
                                        .setExtra(order.getExtra().set(OrderUtil.PLATFORM, order.getPlatform()))

                );
            }
        }
        //商品为代销的订单
        List<ShopOrder> supplierOrderList = TenantShop.disable(
                () -> orderService.getShopOrders(
                        new ShopOrderQueryBO()
                                .setStatus(OrderQueryStatus.UN_DELIVERY)
                                .setSellTypes(Set.of(SellType.CONSIGNMENT))
                                .setOrderNos(
                                        dto.getOrderNos()
                                )
                )
        );

        if (CollUtil.isNotEmpty(supplierOrderList)) {
            //代销商品
            List<ShopOrderItem> supplierOrderItemList = supplierOrderList.stream()
                    .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                    .collect(Collectors.toList());

            //供应商类型 shopType 1 自营
            Map<Long, ShopType> shopTypeMap = getShopTypeMap(supplierOrderItemList, SelfShopTypeEnum.SELF_SUPPLIER);
            //筛选自营供应商的商品
            for (ShopOrder order : supplierOrderList) {
                List<ShopOrderItem> shopOrderItems = order.getShopOrderItems();
                order.setShopOrderItems(shopOrderItems.stream().filter(item -> {
                    ShopType type = shopTypeMap.getOrDefault(item.getSupplierId(), ShopType.ORDINARY);
                    return type.equals(ShopType.SELF_OWNED);
                }).collect(Collectors.toList()));
            }
            Map<String, List<ShopOrder>> groupedSupplierOrders = supplierOrderList.stream().collect(Collectors.groupingBy(ShopOrder::getOrderNo));
            supplierOrders.forEach(
                    order -> order.setShopOrders(groupedSupplierOrders.get(order.getNo()))
                            .setExtra(order.getExtra().set(OrderUtil.PLATFORM, order.getPlatform()))
            );
        }

        return buildOrderPlatFormDeliveryVO(orders, supplierOrders);
    }

    /**
     * 获取店铺、供应商类型
     *
     * @param items 店铺订单商品信息
     * @return 店铺类型
     */
    private Map<Long, ShopType> getShopTypeMap(List<ShopOrderItem> items, SelfShopTypeEnum selfShopType) {
        Set<Long> supplierIds = items.stream()
                .map(SelfShopTypeEnum.SELF_SUPPLIER.equals(selfShopType)
                        ? ShopOrderItem::getSupplierId
                        : ShopOrderItem::getShopId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(supplierIds)) {
            return new HashMap<>(CommonPool.NUMBER_ONE);
        }

        List<ShopInfoVO> shopInfoList = shopRpcService.getShopInfoByShopIdList(supplierIds);

        return shopInfoList.stream()
                .collect(Collectors.toMap(ShopInfoVO::getId, ShopInfoVO::getShopType));
    }

    /**
     * 构建平台发货订单对象
     *
     * @param shopOrders     自营店铺订单
     * @param supplierOrders 自营供应商订单
     * @return 平台发货订单对象
     */
    private OrderPlatFormDeliveryVO buildOrderPlatFormDeliveryVO(List<Order> shopOrders, List<Order> supplierOrders) {
        //过滤掉Order中ShopOrder的ShopOrderItem为空的情况
        shopOrders = shopOrders.stream()
                .filter(order -> order.getShopOrders() != null && order.getShopOrders().stream()
                        .anyMatch(shopOrder -> shopOrder.getShopOrderItems() != null && !shopOrder.getShopOrderItems().isEmpty()))
                .collect(Collectors.toList());

        supplierOrders = supplierOrders.stream()
                .filter(order -> order.getShopOrders() != null && order.getShopOrders().stream()
                        .anyMatch(shopOrder -> shopOrder.getShopOrderItems() != null && !shopOrder.getShopOrderItems().isEmpty()))
                .collect(Collectors.toList());

        OrderPlatFormDeliveryVO vo = new OrderPlatFormDeliveryVO();
        vo.setShopOrders(shopOrders);
        vo.setSupplierOrders(supplierOrders);
        return vo;
    }
}