package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.service.CloseOrderService;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderService;
import com.medusa.gruul.order.service.mp.service.IOrderPaymentService;
import com.medusa.gruul.order.service.mp.service.IOrderService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/6/24
 */
@Service
@RequiredArgsConstructor
public class CloseOrderServiceImpl implements CloseOrderService {


    private final Executor globalExecutor;
    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final QueryOrderService queryOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderItemService shopOrderItemService;
    private final ShopRpcService shopRpcService;
    private CloseOrderService closeOrderService;

    private List<Order> checkOrderStatus(List<Order> orders, OrderStatus currentStatus, OrderStatus targetStatus) {
        if (targetStatus.isSystem()) {
            return orders.stream().filter(order -> order.getStatus() == currentStatus).toList();
        }
        orders.forEach(order -> {
            if (order.getStatus() != currentStatus) {
                throw OrderError.INVALID_ORDER_STATUS.exception();
            }
        });
        return orders;
    }

    @Override
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, batchParamName = "#orderNos", key = "#item")
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Set<String> orderNos, OrderStatus currentStatus, OrderStatus targetStatus) {
        List<Order> orders = checkOrderStatus(
                orderService.lambdaQuery()
                        .select(Order::getType, Order::getActivityId, Order::getStatus, Order::getNo, Order::getBuyerId)
                        .in(Order::getNo, orderNos)
                        .list(),
                currentStatus,
                targetStatus
        );
        if (CollUtil.isEmpty(orders)) {
            return;
        }
        Set<String> needUpdateOrderNos = orders.stream().map(Order::getNo).collect(Collectors.toSet());
        //更新订单
        orderService.lambdaUpdate()
                .in(Order::getNo, needUpdateOrderNos)
                .eq(Order::getStatus, currentStatus)
                .set(Order::getStatus, targetStatus)
                .set(Order::getUpdateTime, LocalDateTime.now())
                .update();
        TenantShop.disable(
                () -> shopOrderService.lambdaUpdate()
                        .in(ShopOrder::getOrderNo, needUpdateOrderNos)
                        .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                        .set(ShopOrder::getStatus, targetStatus.getShopOrderStatus())
                        .set(ShopOrder::getUpdateTime, LocalDateTime.now())
                        .update()
        );
        if (!targetStatus.isClosed()) {
            return;
        }
        globalExecutor.execute(
                () -> {
                    List<ShopOrderItem> items = TenantShop.disable(
                            () -> shopOrderItemService.lambdaQuery()
                                    .in(ShopOrderItem::getOrderNo, needUpdateOrderNos)
                                    .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                                    .list()
                    );
                    Map<String, List<ShopOrderItem>> orderNoItemsMap = items.stream().collect(Collectors.groupingBy(ShopOrderItem::getOrderNo));
                    for (Order order : orders) {
                        String orderNo = order.getNo();
                        orderRabbitService.sendOrderClose(
                                new OrderInfo()
                                        .setCloseType(OrderCloseType.FULL)
                                        .setActivityType(order.getType())
                                        .setActivityId(order.getActivityId())
                                        .setOrderNo(orderNo)
                                        .setBuyerId(order.getBuyerId())
                                        .setSkuStocks(OrderUtil.toSkuStocks(orderNoItemsMap.get(orderNo)))
                        );
                    }

                }
        );

    }

    @Override
    public void updateOrderStatus(String orderNo) {
        Order order = orderService.lambdaQuery()
                .eq(Order::getNo, orderNo)
                .eq(Order::getBuyerId, ISecurity.userMust().getId())
                .one();
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        if (OrderStatus.UNPAID != order.getStatus()) {
            throw OrderError.ORDER_PAID.exception();
        }
        if (order.getStatus().isClosed()) {
            return;
        }
        closeOrderService.updateOrderStatus(Set.of(orderNo), OrderStatus.UNPAID, OrderStatus.BUYER_CLOSED);
    }

    @Override
    @Log("订单支付超时, 系统取消订单")
    public void closeOrderPaidTimeout(String orderNo) {
        closeOrderService.updateOrderStatus(Set.of(orderNo), OrderStatus.UNPAID, OrderStatus.SYSTEM_CLOSED);
    }


    @Override
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, key = "#orderNo")
    @Transactional(rollbackFor = Exception.class)
    public void shopCloseOrder(String orderNo, String shopOrderNo) {
        Order order = Option.of(
                        orderService.lambdaQuery()
                                .select(Order::getType, Order::getActivityId, Order::getNo, Order::getStatus, Order::getBuyerId)
                                .eq(Order::getNo, orderNo)
                                .one()
                )
                .getOrElseThrow(OrderError.ORDER_NOT_EXIST::exception);
        if (OrderStatus.UNPAID != order.getStatus()) {
            throw OrderError.ORDER_PAID.exception();
        }
        //判断是否有其它正常状态店铺订单
        boolean exists = TenantShop.disable(
                () -> shopOrderService.lambdaQuery().eq(ShopOrder::getOrderNo, orderNo).ne(ShopOrder::getNo, shopOrderNo).exists()
        );
        //没有则直接关闭总订单
        if (!exists) {
            closeOrderService.updateOrderStatus(Set.of(orderNo), OrderStatus.UNPAID, OrderStatus.SELLER_CLOSED);
            return;
        }
        OrderPayment unpaidOrderPayment = queryOrderService.getUnpaidOrderPayment(orderNo);
        ShopOrder shopOrder = TenantShop.disable(() -> shopOrderService.lambdaQuery()
                .eq(ShopOrder::getNo, shopOrderNo)
                .eq(ShopOrder::getOrderNo, orderNo)
                .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                .one()
        );
        if (shopOrder == null) {
            throw OrderError.ORDER_CLOSED.exception();
        }
        //取出店铺所有的商品
        Long shopId = shopOrder.getShopId();
        //平台关闭自营店铺订单
        ISecurity.match()
                .ifAnySuperAdmin(
                        secureUser -> {
                            //检查店铺是否是自营店铺
                            ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
                            if (shopInfo == null) {
                                throw OrderError.SHOP_NOT_AVAILABLE.exception();
                            }
                            ShopType shopType = shopInfo.getShopType();
                            //非自营店铺 不允许平台关闭订单
                            if (ShopType.SELF_OWNED != shopType) {
                                throw SystemCode.PARAM_VALID_ERROR.exception();
                            }
                        }
                );
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getOrderNo, orderNo)
                .eq(ShopOrderItem::getShopId, shopId)
                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                .list()
        );
        long payAmount = Math.max(unpaidOrderPayment.getPayAmount() - shopOrder.payAmount(), 0);
        //重新计算费用
        unpaidOrderPayment.setTotalAmount(unpaidOrderPayment.getTotalAmount() - shopOrder.getTotalAmount())
                .setDiscountAmount(unpaidOrderPayment.getDiscountAmount() - shopOrder.getDiscountAmount())
                .setFreightAmount(unpaidOrderPayment.getFreightAmount() - shopOrder.getFreightAmount())
                .setPayAmount(payAmount);
        boolean isNoNeedToPay = payAmount <= 0;
        LocalDateTime now = LocalDateTime.now();
        if (isNoNeedToPay) {
            unpaidOrderPayment.setTransactions(Map.of())
                    .setPayTime(now)
                    .setPayerId(order.getBuyerId())
                    .setType(PayType.BALANCE);
        }
        orderPaymentService.updateById(unpaidOrderPayment);
        //如果需要支付的金额为0 则直接设置为已支付状态
        if (isNoNeedToPay) {
            orderService.lambdaUpdate()
                    .set(Order::getStatus, OrderStatus.PAID)
                    .eq(Order::getNo, orderNo)
                    .update();
        }
        //关闭店铺订单
        TenantShop.disable(
                () -> {
                    shopOrderService.lambdaUpdate()
                            .set(ShopOrder::getStatus, ShopOrderStatus.SELLER_CLOSED)
                            .set(ShopOrder::getUpdateTime, now)
                            .eq(ShopOrder::getId, shopOrder.getId())
                            .eq(ShopOrder::getNo, shopOrderNo)
                            .eq(ShopOrder::getOrderNo, orderNo)
                            .update();
                    shopOrderItemService.lambdaUpdate()
                            .set(ShopOrderItem::getStatus, ItemStatus.CLOSED)
                            .set(ShopOrderItem::getUpdateTime, now)
                            .eq(ShopOrderItem::getOrderNo, orderNo)
                            .eq(ShopOrderItem::getShopId, shopId)
                            .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                            .update();
                }
        );
        //发送 订单关闭mq 需要携带 店铺订单下的所有 商品
        CompletableTask.allOf(
                globalExecutor,
                () -> {
                    if (CollUtil.isEmpty(shopOrderItems)) {
                        return;
                    }
                    orderRabbitService.sendOrderClose(
                            new OrderInfo()
                                    .setActivityType(order.getType())
                                    .setActivityId(order.getActivityId())
                                    .setCloseType(OrderCloseType.SHOP)
                                    .setOrderNo(order.getNo())
                                    .setBuyerId(order.getBuyerId())
                                    .setShopId(shopId)
                                    .setSkuStocks(
                                            OrderUtil.toSkuStocks(shopOrderItems)
                                    )
                    );
                }
        );

    }

    @Autowired
    public void setCloseOrderService(CloseOrderService closeOrderService) {
        this.closeOrderService = closeOrderService;
    }
}
