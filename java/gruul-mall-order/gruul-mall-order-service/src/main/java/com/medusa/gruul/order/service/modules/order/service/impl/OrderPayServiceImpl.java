package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.order.api.addon.rebate.RebateItemPrice;
import com.medusa.gruul.order.api.addon.rebate.RebatePayParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayResponse;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.service.model.dto.OrderPayDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonSupporter;
import com.medusa.gruul.order.service.modules.order.service.OrderPayService;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.enums.PayOrderType;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.model.param.CombineOrderParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/7/28
 */
@Service
@RequiredArgsConstructor
public class OrderPayServiceImpl implements OrderPayService {

    private final Executor globalExecutor;
    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final PaymentRpcService paymentRpcService;
    private final QueryOrderService queryOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderDiscountService orderDiscountService;
    private final IOrderReceiverService orderReceiverService;
    private final IOrderDiscountItemService orderDiscountItemService;
    private final OrderAddonSupporter orderAddonSupporter;
    private final WechatProperties wechatProperties;
    private OrderPayService orderPayService;

    @Override
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, key = "#orderPay.orderNo")
    public PayResult toPay(OrderPayDTO orderPay) {
        String orderNo = orderPay.getOrderNo();
        //获取订单支付信息 未支付订单
        OrderPayment orderPayment = this.queryOrderService.getUnpaidOrderPayment(orderNo);
        //小程序发货,非小程序平台下单的,不允许小程序支付
        Platform platform = ISystem.platformMust();
        PayType payType = orderPay.getPayType();
        if (BooleanUtil.isTrue(wechatProperties.getMiniAppDeliver())
                && platform == Platform.WECHAT_MINI_APP
                && orderPayment.getOrder().getPlatform() != platform
                && payType == PayType.WECHAT
        ) {
            throw new GlobalException("小程序暂不支持调起其他平台订单未支付数据");
        }
        //如果使用过返利支付 或 未启用返利支付 直接发起支付
        if (orderPayment.getExtra().getRebatePay() || !orderPay.getRebate()) {
            return getPayResult(payType, orderPayment.getOrder(), List.of());
        }
        //未使用返利支付过，并且开启了返利支付 使用返利支付
        Tuple2<Long, List<ShopOrder>> payAmountShopOrders = rebatePay(orderPayment);
        //返利支付后，支付金额大于 0 发起剩余金额的支付
        if (payAmountShopOrders._1() > 0) {
            //发起三方付款
            return getPayResult(payType, orderPayment.getOrder(), payAmountShopOrders._2());
        }
        //支付金额小于等于0  无需支付 直接发送订单已支付 mq
        SecureUser<?> secureUser = ISecurity.userMust();
        this.rebatePayNotify(secureUser.getId(), secureUser.getOpenid(), orderNo);
        return new PayResult()
                .setOutTradeNo(orderNo)
                .setNeedPay(false);
    }

    /**
     * 返利支付
     *
     * @param orderPayment 支付信息
     * @return 1. 剩余支付金额，2. 店铺订单列表
     */
    private Tuple2<Long, List<ShopOrder>> rebatePay(OrderPayment orderPayment) {
        String orderNo = orderPayment.getOrderNo();
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(
                () -> shopOrderItemService.lambdaQuery()
                        .select(ShopOrderItem::getId, ShopOrderItem::getShopId, ShopOrderItem::getProductId, ShopOrderItem::getSkuId, ShopOrderItem::getNum, ShopOrderItem::getDealPrice, ShopOrderItem::getFixPrice)
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .list()
        );
        RebatePayResponse rebatePayResponse = orderAddonSupporter.rebateDiscount(
                new RebatePayParam()
                        .setOrderNo(orderNo)
                        .setUserId(ISecurity.userMust().getId())
                        .setOrderItems(shopOrderItems)
        );
        Long payAmount = orderPayment.getPayAmount();
        if (rebatePayResponse == null) {
            return Tuple.of(payAmount, List.of());
        }
        //更新item价格
        Map<ShopProductSkuKey, RebateItemPrice> itemKeyDealPriceMap = rebatePayResponse.getItemKeyDealPriceMap();
        if (CollUtil.isEmpty(itemKeyDealPriceMap)) {
            return Tuple.of(payAmount, List.of());
        }
        Map<Long, Long> shopRebateDiscountMap = new HashMap<>(CommonPool.NUMBER_EIGHT);
        shopOrderItems.forEach(
                item -> {
                    RebateItemPrice rebateItemPrice = itemKeyDealPriceMap.get(item.shopProductSkuKey());
                    if (rebateItemPrice == null) {
                        return;
                    }
                    item.setDealPrice(rebateItemPrice.getDealPrice());
                    item.setFixPrice(rebateItemPrice.getFixPrice());
                    Long shopRebateDiscount = shopRebateDiscountMap.computeIfAbsent(item.getShopId(), k -> 0L);
                    shopRebateDiscountMap.put(item.getShopId(), shopRebateDiscount + rebateItemPrice.getRebateDiscount());
                }
        );
        //保存返利优惠信息
        OrderDiscount rebateDiscount = rebatePayResponse.getRebateDiscount();
        Long rebatePayAmount = rebateDiscount.getSourceAmount();
        //剩余支付金额
        Final<List<ShopOrder>> finalShopOrders = new Final<>();
        long remainPayAmount = payAmount - Math.min(payAmount, rebatePayAmount);
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        () -> {
                            orderDiscountService.save(rebateDiscount);
                            orderDiscountItemService.saveBatch(rebateDiscount.getDiscountItems());
                            TenantShop.disable(() -> shopOrderItemService.updateBatchById(shopOrderItems));
                        },
                        () -> {
                            //查询店铺订单列表
                            List<ShopOrder> shopOrders = TenantShop.disable(() -> shopOrderService.lambdaQuery()
                                    .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                                    .eq(ShopOrder::getOrderNo, orderNo)
                                    .in(ShopOrder::getShopId, shopRebateDiscountMap.keySet())
                                    .list()
                            );
                            shopOrders.forEach(
                                    shopOrder -> {
                                        Long discount = shopRebateDiscountMap.getOrDefault(shopOrder.getShopId(), 0L);
                                        shopOrder.setDiscountAmount(shopOrder.getDiscountAmount() + discount);
                                    }
                            );
                            //更新店铺订单数据
                            TenantShop.disable(() -> shopOrderService.updateBatchById(shopOrders));
                            finalShopOrders.set(shopOrders);
                            //更新支付数据
                            orderPaymentService.lambdaUpdate()
                                    .set(OrderPayment::getDiscountAmount, orderPayment.getDiscountAmount() + rebatePayAmount)
                                    .set(OrderPayment::getPayAmount, remainPayAmount)
                                    .set(OrderPayment::getExtra, JSON.toJSONString(orderPayment.getExtra().setRebatePay(true)))
                                    .eq(OrderPayment::getOrderNo, orderNo)
                                    .update();
                        }
                )
        );
        return Tuple.of(remainPayAmount, finalShopOrders.get());
    }

    private void rebatePayNotify(Long userId, String openid, String orderNo) {
        PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
        paymentInfo.setOrderNum(orderNo);
        paymentInfo.setUserId(userId);
        paymentInfo.setPayType(PayType.BALANCE);
        paymentInfo.setTotalFee(0L);
        paymentInfo.setOpenId(openid);
        orderPayService.payNotify(
                new PayNotifyResultDTO()
                        .setPayOrderType(PayOrderType.COMMON)
                        .setBusinessParams(paymentInfo)
        );
    }

    @Override
    @Log("支付回调")
    @Redisson(name = OrderConstant.ORDER_EDIT_LOCK_KEY, key = "#payNotifyResult.businessParams.orderNum")
    @Transactional(rollbackFor = Exception.class)
    public void payNotify(PayNotifyResultDTO payNotifyResult) {
        PaymentInfoDTO businessParams = payNotifyResult.getBusinessParams();
        String orderNo = businessParams.getOrderNum();
        //更新 支付信息
        OrderPayment orderPayment;
        try {
            orderPayment = this.queryOrderService.getUnpaidOrderPayment(orderNo);
        } catch (GlobalException globalException) {
            //如果订单不能支付直接走退款流程
            paymentRpcService.refundRequest(
                    new RefundRequestDTO()
                            .setOrderNum(orderNo)
                            .setShopId(0L)
                            .setRefundFee(businessParams.getTotalFee())
                            .setDesc("订单超时支付退款")
            );
            return;
        }
        Order order = orderPayment.getOrder();
        //以支付平台类型为准，更新订单平台
        Platform platform = businessParams.getPayPlatform();
        if (null != platform) {
            orderService.lambdaUpdate().eq(Order::getNo, order.getNo()).set(Order::getPlatform, platform).update();
        }
        Long buyerId = order.getBuyerId();
        Long payerId = businessParams.getUserId();
        Map<Long, Transaction> shopIdTransactionMap = payNotifyResult.getShopIdTransactionMap();
        boolean success = this.orderPaymentService.updateById(
                orderPayment.setTransactions(shopIdTransactionMap)
                        .setPayTime(LocalDateTime.now())
                        .setPayerId(payerId)
                        .setType(businessParams.getPayType())
        );
        if (!success) {
            this.payNotify(payNotifyResult);
            return;
        }
        //更新订单支付状态
        this.orderService.lambdaUpdate()
                .set(Order::getStatus, order.paidStatus())
                .set(Order::getUpdateTime, LocalDateTime.now())
                .eq(Order::getStatus, OrderStatus.UNPAID)
                .eq(Order::getNo, orderNo)
                .update();
        LocalDateTime notifyTime = LocalDateTime.now();
        Long totalFee = businessParams.getTotalFee();

        CompletableTask.allOf(
                this.globalExecutor,
                () -> {
                    List<ShopOrder> shopOrders = TenantShop.disable(
                            () -> this.shopOrderService.lambdaQuery()
                                    .select(ShopOrder::getShopId, ShopOrder::getTotalAmount, ShopOrder::getDiscountAmount, ShopOrder::getFreightAmount)
                                    .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                                    .eq(ShopOrder::getOrderNo, orderNo)
                                    .list()
                    );
                    if (CollUtil.isEmpty(shopOrders)) {
                        return;
                    }
                    orderRabbitService.sendOrderPaid(
                            new OrderPaidBroadcastDTO()
                                    .setDistributionMode(order.getDistributionMode())
                                    .setActivityType(order.getType())
                                    .setActivityId(order.getActivityId())
                                    .setOrderNo(orderNo)
                                    .setBuyerId(buyerId)
                                    .setBuyerNickname(order.getBuyerNickname())
                                    .setBuyerAvatar(order.getBuyerAvatar())
                                    .setPayerId(payerId)
                                    .setPayAmount(totalFee)
                                    .setOrderPayment(orderPayment.setOrder(null))
                                    .setNotifyTime(notifyTime)
                                    .setExtra(order.getExtra())
                                    .setShopPayAmounts(
                                            shopOrders.stream()
                                                    .map(shopOrder -> new OrderPaidBroadcastDTO.ShopPayAmountDTO()
                                                            .setShopId(shopOrder.getShopId())
                                                            .setAmount(shopOrder.payAmount())
                                                            .setFreightAmount(shopOrder.getFreightAmount())
                                                            .setDiscountAmount(shopOrder.getDiscountAmount()))
                                                    .collect(Collectors.toList())
                                    )
                    );
                },
                () -> {
                    String openId = payNotifyResult.getBusinessParams().getOpenId();
                    if (StrUtil.isEmpty(openId)) {
                        return;
                    }
                    // 发送小程序订阅消息
                    SubscribeAppletMsgDTO subscribeApplet = new SubscribeAppletMsgDTO();
                    subscribeApplet.setOpenid(openId);
                    subscribeApplet.setSubscribeMsg(SubscribeMsg.ORDER_PAY);
                    Map<String, String> data = new HashMap<>(CommonPool.NUMBER_FIVE);
                    data.put("character_string1", order.getNo());
                    data.put("amount2", BigDecimal.valueOf(orderPayment.getPayAmount()).divide(
                            CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND,
                            CommonPool.NUMBER_TWO,
                            RoundingMode.DOWN).toString());
                    data.put("date5", notifyTime.toString());
                    Optional.ofNullable(TenantShop.disable(() -> orderReceiverService.lambdaQuery()
                                    .select(OrderReceiver::getName, OrderReceiver::getAddress)
                                    .eq(OrderReceiver::getOrderNo, orderNo)
                                    .one()))
                            .map(orderReceiver -> {
                                data.put("name3", orderReceiver.getName());
                                data.put("thing4", orderReceiver.getAddress());
                                return data;
                            });
                    subscribeApplet.setData(data);
                    orderRabbitService.sendAppletSubscribeMsg(subscribeApplet);
                }

        );
    }


    private PayResult getPayResult(PayType payType, Order order, @Nullable List<ShopOrder> shopOrders) {
        String orderNo = order.getNo();
        OrderTimeout timeout = order.getTimeout();
        SecureUser<?> secureUser = ISecurity.userMust();
        //获取补差金额
        return paymentRpcService.combinePay(
                new CombineOrderParam()
                        .setOrderNo(orderNo)
                        .setPayType(payType)
                        .setSeconds(timeout.getPayTimeout() + CommonPool.NUMBER_THIRTY)
                        .setAttach("订单编号：" + orderNo)
                        .setConsumer(
                                new CombineOrderParam.Consumer()
                                        .setPlatform(ISystem.platformMust())
                                        .setUserId(secureUser.getId())
                                        .setOpenId(secureUser.getOpenid())
                                        .setDeviceId(ISystem.deviceIdMust())
                                        .setClientIp(ISystem.ipMust())
                        )
                        .setSubOrders(this.subOrders(orderNo, shopOrders))
                        .setExchange(OrderRabbit.ORDER_PAID_CALLBACK.exchange())
                        .setRouteKey(OrderRabbit.ORDER_PAID_CALLBACK.routingKey())
        );
    }

    public List<CombineOrderParam.SubOrder> subOrders(String orderNo, @Nullable List<ShopOrder> shopOrders) {
        if (CollUtil.isEmpty(shopOrders)) {
            shopOrders = TenantShop.disable(
                    () -> shopOrderService.lambdaQuery()
                            .eq(ShopOrder::getOrderNo, orderNo)
                            .eq(ShopOrder::getStatus, ShopOrderStatus.OK)
                            .list()
            );
            if (CollUtil.isEmpty(shopOrders)) {
                throw OrderError.ORDER_NOT_EXIST.exception();
            }
        }
        //Map<Long, Long> shopIdSubsidyAmountMap = subsidyAmountMap(orderNo);
        return shopOrders.stream()
                .map(
                        shopOrder -> {
                            Long payAmount = shopOrder.payAmount();
                            return new CombineOrderParam.SubOrder()
                                    .setShopId(shopOrder.getShopId())
                                    .setSubOrderNo(shopOrder.getNo())
                                    .setTotalAmount(payAmount)
                                    .setCurrency(FeeType.CNY)
                                    .setDescription(StrUtil.format("{}-{}", shopOrder.getShopName(), "购买商品"))
                                    .setProfitSharing(true)
                                    .setSubsidyAmount(0L);
                        }
                ).toList();
    }


    @Lazy
    @Autowired
    public void setOrderPayService(OrderPayService orderPayService) {
        this.orderPayService = orderPayService;
    }
}
