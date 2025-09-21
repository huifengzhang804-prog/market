package com.medusa.gruul.order.service.modules.deliver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.*;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import com.medusa.gruul.order.service.model.dto.OrdersDeliveryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.ShopOrderUndeliveredVO;
import com.medusa.gruul.order.service.modules.deliver.model.DeliveryParam;
import com.medusa.gruul.order.service.modules.deliver.service.ShopOrderDeliverService;
import com.medusa.gruul.order.service.modules.deliver.strategy.DeliveryStrategyFactory;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/7/26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShopOrderDeliverServiceImpl implements ShopOrderDeliverService {

    private final Executor globalExecutor;
    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IOrderReceiverService orderReceiverService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderItemService shopOrderItemService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final WechatProperties wechatProperties;
    private final DeliveryStrategyFactory deliveryStrategyFactory;
    private ShopOrderDeliverService shopOrderDeliverService;

    /**
     * 商品店铺id
     *
     * @param platformDelivery 是否平台发货
     * @param shopId           当前登录店铺id
     * @param shopOrder        订单信息里的shopId
     * @return 店铺id
     */
    private static Long getShopId(boolean platformDelivery, Long shopId, ShopOrder shopOrder) {
        return platformDelivery ? shopOrder.getShopId() : shopId;
    }

    @Override
    public ShopOrderUndeliveredVO undelivered(DeliveryQueryBO deliveryMatch) {
        return new ShopOrderUndeliveredVO()
                .setOrderReceiver(orderReceiverService.getCurrentOrderReceiver(deliveryMatch.getOrderNo(), deliveryMatch.getShopOrderNo()))
                .setShopOrderItems(TenantShop.disable(() -> shopOrderItemService.undelivered(deliveryMatch)))
                .setExtra(
                        TenantShop.disable(() -> {
                            Optional<Order> optionalOrder = orderService.lambdaQuery()
                                    .eq(Order::getNo, deliveryMatch.getOrderNo())
                                    .list().stream().findFirst();
                            if (optionalOrder.isPresent()) {
                                Order order = optionalOrder.get();
                                JSONObject extra = order.getExtra();
                                if (extra != null) {
                                    extra.set(OrderUtil.PLATFORM, order.getPlatform());
                                }
                                return extra;
                            } else {
                                // 如果没有找到订单，返回一个默认值
                                return new JSONObject();
                            }
                        })
                );
    }

    @Override
    public List<ShopOrderItem> delivered(DeliveryQueryBO deliveryMatch) {
        Supplier<List<ShopOrderItem>> deliveredItems = () -> {
            ShopOrder shopOrder = shopOrderService.lambdaQuery()
                    .eq(ShopOrder::getOrderNo, deliveryMatch.getOrderNo())
                    .eq(ShopOrder::getNo, deliveryMatch.getShopOrderNo())
                    .one();
            return Option.of(shopOrder)
                    .map(
                            shop -> shopOrderItemService.lambdaQuery()
                                    .eq(ShopOrderItem::getOrderNo, deliveryMatch.getOrderNo())
                                    .ne(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                                    .isNotNull(ShopOrderItem::getPackageId)
                                    .list()
                    ).getOrElseThrow(SystemCode.DATA_NOT_EXIST::exception);
        };
        return Option.of(deliveryMatch.getShopId())
                .map(shopId -> ISystem.shopId(shopId, deliveredItems))
                .getOrElse(deliveredItems);
    }

    @Override
    public Option<ShopOrderPackage> deliveredPackageFirst(String orderNo, String shopOrderNo, Long packageId) {
        ShopOrderPackageQueryBO query = new ShopOrderPackageQueryBO()
                .setShopOrderNo(shopOrderNo)
                .setLimitOne(Boolean.TRUE)
                .setPackageId(packageId);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));

        List<ShopOrderPackage> shopOrderPackages = TenantShop.disable(() -> shopOrderPackageService.deliveredPackages(orderNo, query));
        return Option.when(CollUtil.isNotEmpty(shopOrderPackages), () -> shopOrderPackages.get(0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(DeliveryQueryBO deliveryMatch, OrderDeliveryDTO orderDelivery) {
        orderDelivery.validParam();
        //是否平台发货
        boolean platformDelivery = ShopDeliverModeSettingsEnum.PLATFORM == deliveryMatch.getShopDeliverModeSettings();
        String orderNo = deliveryMatch.getOrderNo();
        Order order = orderService.lambdaQuery()
                .select(Order::getStatus, Order::getDistributionMode, Order::getTimeout, Order::getBuyerId, Order::getPlatform, Order::getExtra)
                .eq(Order::getNo, orderNo)
                .one();
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.dataEx(orderNo);
        }
        DistributionMode distributionMode = order.getDistributionMode();
        //校验下单时的配送方式 是否和发货方式匹配
        DeliverType deliverType = orderDelivery.getDeliverType();
        Set<DistributionMode> matchedModes = switch (deliverType) {
            case EXPRESS, PRINT_EXPRESS -> Set.of(DistributionMode.EXPRESS);
            case WITHOUT -> Set.of(DistributionMode.VIRTUAL, DistributionMode.SHOP_STORE);
            case IC_OPEN, IC_MERCHANT -> Set.of(DistributionMode.INTRA_CITY_DISTRIBUTION);
        };
        if (!matchedModes.contains(distributionMode)) {
            throw OrderError.NOT_SUPPORT_DISTRIBUTION_MODE.dataEx(orderNo);
        }

        deliveryMatch.setPlatform(order.getPlatform());
        OrderStatus status = order.getStatus();
        if (status.isClosed()) {
            throw OrderError.ORDER_CLOSED.dataEx(orderNo);
        }
        if (OrderStatus.UNPAID == status) {
            throw OrderError.ORDER_NOT_PAID.dataEx(orderNo);
        }
        //门店订单只能门店管理员发货
        if (DistributionMode.SHOP_STORE == distributionMode && !ISecurity.anyRole(Roles.SHOP_STORE)) {
            throw SecureCodes.ACCESS_DENIED.dataEx(orderNo);
        }
        Long shopId = deliveryMatch.getShopId();
        Long targetShopId = platformDelivery ? orderDelivery.getShopId() : shopId;
        ShopOrder shopOrder = TenantShop.disable(
                () -> shopOrderService.lambdaQuery()
                        .select(ShopOrder::getNo, ShopOrder::getStatus, ShopOrder::getStatus, ShopOrder::getShopId)
                        .eq(ShopOrder::getShopId, targetShopId)
                        .eq(ShopOrder::getOrderNo, orderNo)
                        .one()
        );
        if (shopOrder == null) {
            throw OrderError.ORDER_NOT_EXIST.dataEx(orderNo);
        }
        //平台发货，shopId修改
        deliveryMatch.setShopId(getShopId(platformDelivery, shopId, shopOrder));
        if (shopOrder.getStatus().isClosed()) {
            throw OrderError.ORDER_CLOSED.dataEx(orderNo);
        }
        Long supplierId = deliveryMatch.getSupplierId();
        //平台发货，自营供应商，筛选自营供应商的商品，否则筛选自营店铺的商品
        boolean selfSupplier = SelfShopTypeEnum.SELF_SUPPLIER == orderDelivery.getSelfShopType();
        List<ShopOrderItem> items = getShopOrderItems(
                deliveryMatch, orderDelivery, platformDelivery,
                orderNo, shopId, supplierId, selfSupplier, true
        );
        //如果供应商 id 为空并且不是平台发货 说明是店铺发货 店铺无法给代销商品发货
        if (supplierId == null && !platformDelivery && items.stream().anyMatch(item -> item.getSellType() == SellType.CONSIGNMENT)) {
            throw OrderError.CONSIGNMENT_GOODS_CANNOT_DELIVER.dataEx(orderNo);
        }
        if (CollUtil.isEmpty(items)) {
            throw OrderError.NOT_CONTAIN_WAIT_DELIVER_GOODS.dataEx(orderNo);
        }
        deliveryMatch.setShopOrderNo(shopOrder.getNo());
        //同城配送，不允许拆单
        if (DistributionMode.INTRA_CITY_DISTRIBUTION == distributionMode) {
            List<ShopOrderItem> allItems = getShopOrderItems(deliveryMatch, orderDelivery, platformDelivery, orderNo, shopId,
                    supplierId, selfSupplier, false);
            if (items.stream().mapToInt(ShopOrderItem::getNum).sum() != allItems.stream().mapToInt(ShopOrderItem::getNum).sum()) {
                throw OrderError.IC_ORDER_CANNOT_SPLIT.dataEx(orderNo);
            }
        }
        //更新发货时间
        TenantShop.disable(
                () -> shopOrderService.lambdaUpdate()
                        .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("deliverTime", LocalDateTime.now().format(DatePattern.NORM_DATETIME_FORMATTER))))
                        .eq(ShopOrder::getShopId, targetShopId)
                        .eq(ShopOrder::getOrderNo, orderNo)
                        .update()
        );
        //发货
        ShopOrderPackage shopOrderPackage;
        try {
            shopOrderPackage = deliveryStrategyFactory.execute(
                    deliverType,
                    new DeliveryParam()
                            .setDeliveryMatch(deliveryMatch)
                            .setOrderDelivery(orderDelivery)
            );
        } catch (Exception ex) {
            log.error("发货失败", ex);
            GlobalException globalException = ex instanceof GlobalException exception ? exception : new GlobalException("发货失败", ex);
            globalException.setData(orderNo);
            throw globalException;
        }

        log.debug("shopId {},supplierId{},shopOrder-shopId,{}", shopId, supplierId, shopOrder.getShopId());
        //发送延迟消息确认收货、发送订单已发货 mq
        if (shopOrderPackage.isSendDelayConfirm()) {
            sendDelayMessage(deliveryMatch, platformDelivery, orderNo, order, shopId, shopOrder, supplierId, items, shopOrderPackage);
        }
    }

    /**
     * 获取店铺商品信息
     */
    private List<ShopOrderItem> getShopOrderItems(DeliveryQueryBO deliveryMatch, OrderDeliveryDTO orderDelivery,
                                                  boolean platformDelivery, String orderNo, Long shopId,
                                                  Long supplierId, boolean selfSupplier, boolean needId) {
        return TenantShop.disable(
                () -> shopOrderItemService.lambdaQuery()
                        .select(!needId, ShopOrderItem::getNum)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .eq(!platformDelivery, ShopOrderItem::getShopId, shopId)
                        .eq(deliveryMatch.getSellType() != null, ShopOrderItem::getSellType, deliveryMatch.getSellType())
                        .eq(!platformDelivery && supplierId != null, ShopOrderItem::getSupplierId, supplierId)
                        //平台发货，自营供应商商品
                        .eq(platformDelivery && selfSupplier, ShopOrderItem::getSellType, SellType.CONSIGNMENT)
                        //平台发货，自营店铺商品
                        .ne(platformDelivery && !selfSupplier, ShopOrderItem::getSellType, SellType.CONSIGNMENT)
                        .in(needId, ShopOrderItem::getId, orderDelivery.getItems().stream().map(OrderDeliveryItemDTO::getItemId).collect(Collectors.toSet()))
                        .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                        .list()
        );
    }

    /**
     * 发送延迟消息确认收货、发送订单已发货 mq
     *
     * @param deliveryMatch    订单匹配条件
     * @param platformDelivery 是否平台发货
     * @param orderNo          订单号
     * @param order            订单
     * @param shopId           店铺id
     * @param shopOrder        店铺订单
     * @param supplierId       供应商id
     * @param items            订单商品
     * @param shopOrderPackage 订单包裹
     */
    private void sendDelayMessage(DeliveryQueryBO deliveryMatch, boolean platformDelivery, String orderNo, Order order,
                                  Long shopId, ShopOrder shopOrder, Long supplierId, List<ShopOrderItem> items,
                                  ShopOrderPackage shopOrderPackage) {
        IManualTransaction.afterCommit(
                () -> CompletableTask.allOf(
                        globalExecutor,
                        //尝试发送延迟收货 mq
                        () -> orderRabbitService.sendDelayConfirmReceive(
                                true,
                                new OrderPackageKeyDTO().setBuyerId(order.getBuyerId())
                                        .setOrderNo(orderNo)
                                        .setShopId(getShopId(platformDelivery, shopId, shopOrder)),
                                order.getTimeout().getConfirmTimeoutMills(),
                                getShopId(platformDelivery, shopId, shopOrder)
                        ),
                        () -> {
                            OrderPayment orderPayment = orderPaymentService.lambdaQuery()
                                    .select(OrderPayment::getTransactions, OrderPayment::getType)
                                    .eq(OrderPayment::getOrderNo, orderNo).one();
                            OrderPackageKeyDTO orderPackageKeyDTO = new OrderPackageKeyDTO()
                                    .setDistributionMode(order.getDistributionMode())
                                    .setBuyerId(order.getBuyerId())
                                    .setOrderNo(orderNo)
                                    .setShopOrderNo(deliveryMatch.getShopOrderNo())
                                    .setShopId(SellType.CONSIGNMENT == deliveryMatch.getSellType() ? supplierId : getShopId(platformDelivery, shopId, shopOrder))
                                    .setExtra(order.getExtra())
                                    .setPackageId(shopOrderPackage.getId())
                                    .setTransaction(orderPayment.transaction(getShopId(platformDelivery, shopId, shopOrder)));
                            //计算已支付运费与优惠运费
                            long paidFreight = 0, discountFreight = 0;
                            for (ShopOrderItem item : items) {
                                paidFreight += item.getFreightPrice();
                                discountFreight += item.getExtra().getDiscountFreight();
                            }
                            orderPackageKeyDTO.setPaidFreight(paidFreight);
                            orderPackageKeyDTO.setDiscountFreight(discountFreight);
                            orderRabbitService.sendOrderDeliver(orderPackageKeyDTO);
                            //小程序发货
                            if (wechatProperties.getMiniAppDeliver()
                                    && Platform.WECHAT_MINI_APP == deliveryMatch.getPlatform() && orderPayment.getType() == PayType.WECHAT) {
                                orderPackageKeyDTO.setLogisticsType(OrderUtil.getLogisticsType(order.getDistributionMode()));
                                orderRabbitService.sendMiniAppOrderDeliver(orderPackageKeyDTO);
                            }
                        }
                )
        );
    }

    @Override
    public void batchDeliver(DeliveryQueryBO deliveryMatch, List<OrdersDeliveryDTO> ordersDeliveries) {
        boolean supplierDelivery = deliveryMatch.getSellType() == SellType.CONSIGNMENT && deliveryMatch.getSupplierId() != null;
        //手动开启事务 不用走动态代理
        ordersDeliveries.forEach(
                ordersDelivery -> {
                    if (supplierDelivery) {
                        deliveryMatch.setShopId(ordersDelivery.getShopId());
                    }
                    Long shopId = ordersDelivery.getShopId();
                    Long orderShopId = ordersDelivery.getOrderDelivery().getShopId();
                    SelfShopTypeEnum selfShopType = ordersDelivery.getSelfShopType();
                    ordersDelivery.getOrderDelivery().setSelfShopType(selfShopType);
                    ordersDelivery.getOrderDelivery().setShopId(orderShopId == null ? shopId : orderShopId);
                    shopOrderDeliverService.deliver(
                            deliveryMatch.setOrderNo(ordersDelivery.getOrderNo()),
                            ordersDelivery.getOrderDelivery()
                    );
                }
        );
    }


    @Autowired
    public void setShopOrderDeliverService(ShopOrderDeliverService shopOrderDeliverService) {
        this.shopOrderDeliverService = shopOrderDeliverService;
    }
}