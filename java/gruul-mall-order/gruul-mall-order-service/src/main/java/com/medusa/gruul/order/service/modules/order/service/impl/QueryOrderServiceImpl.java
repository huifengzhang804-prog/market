package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.WxMaOrderShippingInfoNotifyConfirmRequest;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.order.service.model.bo.OrderQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderPackageQueryBO;
import com.medusa.gruul.order.service.model.bo.ShopOrderQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderCountQueryDTO;
import com.medusa.gruul.order.service.model.dto.OrderDetailQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.BuyerOrderCountVO;
import com.medusa.gruul.order.service.model.vo.OrderCountVO;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 订单查询服务实现类
 *
 * @author 张治保
 * date 2022/6/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryOrderServiceImpl implements QueryOrderService {

    private final Executor globalExecutor;
    private final IOrderService orderService;
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;
    private final IOrderDiscountService orderDiscountService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderPaymentService orderPaymentService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final PaymentRpcService paymentRpcService;
    private final WechatProperties wechatProperties;
    private final WxMaService wxMaService;

    private static void dealRemark(Order order) {
        if (order == null) {
            return;
        }
        for (ShopOrder shopOrder : order.getShopOrders()) {
            ShopOrder.Remark remark = shopOrder.getRemark();
            if (remark != null) {
                remark.clearDistractor();
            }
        }
    }

    @Override
    public Option<Order> orderDetail(OrderDetailQueryDTO orderDetailQuery) {
        OrderQueryBO query = new OrderQueryBO().setOrderNo(orderDetailQuery.getOrderNo())
                .setShopOrderShopId(orderDetailQuery.getShopId())
                .setUsePackage(BooleanUtil.isTrue(orderDetailQuery.getUsePackage()))
                .setPackageId(orderDetailQuery.getPackageId());
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> query.setShopOrderSupplierId(secureUser.getShopId()).setSellType(SellType.CONSIGNMENT))
                .ifAnyShopAdmin(secureUser -> query.setShopOrderShopId(secureUser.getShopId()))
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()));

        Order order = TenantShop.disable(() -> orderService.getOrder(query));
        dealRemark(order);
        return Option.of(order)
                .peek(ord -> {
                    String orderNo = query.getOrderNo();
                    CompletableTask.getOrThrowException(
                            CompletableTask.allOf(
                                    globalExecutor,
                                    () -> ord.setOrderDiscounts(
                                            TenantShop.disable(() -> orderDiscountService.orderDiscounts(orderDetailQuery))
                                    ),
                                    () -> {
                                        if (DistributionMode.INTRA_CITY_DISTRIBUTION == order.getDistributionMode()) {
                                            ICStatus icStatus = MapUtil.emptyIfNull(orderAddonDistributionSupporter.icOrderStatus(Set.of(orderNo), false)).get(orderNo);
                                            if (icStatus != null) {
                                                order.setIcStatus(icStatus.getStatus());
                                                order.setIcStatusDesc(icStatus.getStatusDesc());
                                            }
                                        }
                                        if (BooleanUtil.isTrue(orderDetailQuery.getUsePackage()) && query.getPackageId() == null) {
                                            return;
                                        }
                                        ord.setShopOrderPackages(
                                                TenantShop.disable(() -> shopOrderPackageService.deliveredPackages(
                                                        orderNo,
                                                        new ShopOrderPackageQueryBO()
                                                                .setShopId(query.getShopOrderShopId())
                                                                .setBuyerId(query.getBuyerId())
                                                                .setLimitOne(Boolean.FALSE)
                                                                .setPackageId(query.getPackageId())

                                                ))

                                        );

                                    }
                            )
                    );
                });
    }

    @Override
    public Option<ShopOrder> getShopOrderByNo(String orderNo, String shopOrderNo) {
        ShopOrderQueryBO query = new ShopOrderQueryBO()
                .setOrderNos(Collections.singleton(orderNo))
                .setShopOrderNo(shopOrderNo);
        ISecurity.match()
                .ifUser(secureUser -> query.setBuyerId(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> query.setShopIds(Set.of(secureUser.getShopId())))
                .ifAnySupplierAdmin(
                        secureUser -> query.setSupplierId(secureUser.getShopId())
                                .setSellTypes(Set.of(SellType.CONSIGNMENT))
                );
        List<ShopOrder> shopOrders = TenantShop.disable(() -> orderService.getShopOrders(query));
        return Option.when(CollUtil.isNotEmpty(shopOrders), () -> shopOrders.get(0));
    }

    @Override
    public OrderPayment getUnpaidOrderPayment(String orderNo) {
        Order order = orderService.lambdaQuery().eq(Order::getNo, orderNo).one();
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        OrderStatus orderStatus = order.getStatus();
        if (OrderStatus.PAID == orderStatus) {
            throw OrderError.ORDER_PAID.exception();
        }
        if (OrderStatus.UNPAID != orderStatus) {
            throw OrderError.ORDER_CANNOT_PAY.exception();
        }
        OrderPayment orderPayment = orderPaymentService.lambdaQuery()
                .eq(OrderPayment::getOrderNo, orderNo)
                .one();
        if (orderPayment == null) {
            throw OrderError.ORDER_CANNOT_PAY.exception();
        }
        return orderPayment.setOrder(order);
    }

    @Override
    public BuyerOrderCountVO buyerOrderCount() {
        return TenantShop.disable(() -> orderService.buyerOrderCount(ISecurity.userMust().getId()));
    }

    @Override
    public boolean orderCreation(String orderNo) {
        return BooleanUtil.isFalse(
                RedisUtil.getRedisTemplate()
                        .hasKey(RedisUtil.key(OrderConstant.ORDER_CACHE_KEY, ISecurity.userMust().getId(), orderNo))
        );
    }

    @Override
    public Option<ShopOrderItem> getShopOrderItem(String orderNo, Long itemId) {
        return Option.of(
                TenantShop.disable(
                        () -> shopOrderItemService.lambdaQuery()
                                .eq(ShopOrderItem::getOrderNo, orderNo)
                                .eq(ShopOrderItem::getId, itemId)
                                .one()
                )
        );
    }

    @Override
    public OrderCountVO orderCount(OrderCountQueryDTO query) {
        OrderCountVO count = TenantShop.disable(() -> orderService.orderCount(query));
        return count == null ? new OrderCountVO() : count;
    }

    /**
     * 1 是小程序下单 且是微信支付 且开启了小程序发货上传配置 才可以操作
     * 2 物流查询所有的订单服务状态都是已签收 但是我们的服务还没有确认收货才能调用确认收货提醒接口
     * 3 确认收货提醒只能调用一次
     * @param orderNo
     */
    @Override
    public void miniAppDeliverConfirm(String orderNo) {
        if (!wechatProperties.getMiniAppDeliver()) {
            log.info(" orderNo= {} 平台未开启小程序发货上传,不调用确认收货提醒接口");
            throw OrderError.MIN_APP_DELIVER_NOT_OPEN.exception();
        }
        Order order = TenantShop.disable(() -> {
            return orderService.lambdaQuery()
                    .eq(Order::getNo, orderNo)
                    .one();
        });
        if (order == null) {
            throw OrderError.ORDER_NOT_EXIST.exception();
        }
        if (!Platform.WECHAT_MP.equals(order.getPlatform())) {
            log.info(" orderNo= {} 不是微信小程序下单,不调用确认收货提醒接口");
            throw OrderError.MIN_APP_DELIVER_CONFIRM_NOT_SUPPORT.exception();
        }
        //是否已经提醒收货了
        Boolean confirmd= order.getExtra().getBool(OrderConstant.ORDER_MIN_APP_DELIVER_CONFIRM);
        if (confirmd) {
            log.info(" orderNo= {} 已经调用过确认收货提醒接口 不能再次调用");
            return;
        }
        PaymentInfo paymentInfo = paymentRpcService.getPaymentInfo(orderNo);
        if (!paymentInfo.getPayType().equals(PayType.WECHAT)) {
            //不支持非微信支付
            log.info(" orderNo= {} 不支持非微信支付,不调用确认收货提醒接口");
            throw OrderError.MIN_APP_DELIVER_CONFIRM_NOT_SUPPORT.exception();
        }
        //待收货包裹数量
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> {
            return shopOrderItemService.lambdaQuery()
                    .eq(ShopOrderItem::getOrderNo, orderNo)
                    .list();
        });
        //未发货数量
        long undeliveredCount = shopOrderItems.stream().filter(x -> Objects.isNull(x.getPackageId())).count();
        if (undeliveredCount>0) {
            //有未发货包裹 则不进行确认收货
            log.info(" orderNo= {} 存在未发货的包裹数量 = {} 不调用确认收货提醒接口");
            return;
        }
        //待收货包裹数量
        long waitForReceiveCount = shopOrderItems.stream().
                filter(x -> PackageStatus.WAITING_FOR_RECEIVE.equals(x.getPackageStatus())).count();
        if (waitForReceiveCount == 0) {
            //没有待收货包裹 则不进行确认收货
            log.info(" orderNo= {} 没有待收货的包裹数量 = {} 不调用确认收货提醒接口");
            return;
        }
        List<ShopOrderPackage> shopOrderPackageList = shopOrderPackageService.lambdaQuery()
                .in(ShopOrderPackage::getOrderNo, orderNo)
                .list();
        for (ShopOrderPackage shopOrderPackage : shopOrderPackageList) {
            Boolean signed = orderAddonDistributionSupporter.queryPackageSignedInfo(shopOrderPackage.getExpressCompanyCode(),
                    shopOrderPackage.getExpressNo(), shopOrderPackage.getReceiverMobile());
            if (!signed) {
                //一个订单的多个包裹 只要存在一个未签收  则不调用确认收货提醒接口
                return;
            }
        }
        sendMinAppDeliverConfirm(orderNo,paymentInfo);

    }

    /**
     * 发送小程序确认收货提醒接口
     * @param orderNo
     * @param paymentInfo
     */
    @Redisson(name = OrderConstant.ORDER_MIN_APP_DELIVER_CONFIRM, key = "#orderNo")
    private void sendMinAppDeliverConfirm(String orderNo,PaymentInfo paymentInfo) {
        try {
            WxMaOrderShippingService wxMaOrderShippingService = new WxMaOrderShippingServiceImpl(wxMaService);
            WxMaOrderShippingIsTradeManagedResponse tradeManagedResponse =
                    wxMaOrderShippingService.isTradeManaged(wechatProperties.getAppId());
            if (!tradeManagedResponse.getTradeManaged()) {
                log.error("APPID【{}】小程序未开通发货信息管理服务,订单ID：{}",
                        wechatProperties.getAppId(), orderNo);
                return;
            }
            WxMaOrderShippingInfoNotifyConfirmRequest request = new WxMaOrderShippingInfoNotifyConfirmRequest();
            request.setTransactionId(paymentInfo.getTransactionId());
            request.setReceivedTime(System.currentTimeMillis());
            WxMaOrderShippingInfoBaseResponse response = wxMaOrderShippingService.notifyConfirmReceive(request);
            log.info("确认收货提醒接口返回：errorCode={},errorMsg={}", response.getErrCode(),response.getErrMsg());
            if (CommonPool.NUMBER_ZERO==response.getErrCode()) {
                orderService.lambdaUpdate()
                        .setSql(SqlHelper.renderJsonSetSql("extra",
                                Tuple.of(OrderConstant.ORDER_MIN_APP_DELIVER_CONFIRM,Boolean.TRUE)))
                        .eq(Order::getNo, orderNo)
                        .update();
            }
        } catch (Exception e) {
            log.error("确认收货提醒接口异常", e);
        }
    }
}