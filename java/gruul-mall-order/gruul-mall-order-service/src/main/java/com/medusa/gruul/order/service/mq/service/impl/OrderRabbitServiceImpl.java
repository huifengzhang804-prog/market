package com.medusa.gruul.order.service.mq.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.WxMaOrderShippingInfoNotifyConfirmRequest;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import com.medusa.gruul.carrier.pigeon.api.enums.PigeonRabbit;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.model.wechat.logistics.DeliveryMode;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.order.api.model.wechat.logistics.OrderUploadType;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2023/6/29
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderRabbitServiceImpl implements OrderRabbitService {

    private final RabbitTemplate rabbitTemplate;
    private final IShopOrderService shopOrderService;
    private final IShopOrderItemService shopOrderItemService;
    private final WxMaService wxMaService;
    private final WechatProperties wechatProperties;
    private final PaymentRpcService paymentRpcService;


    @Override
    public void sendOrderClose(OrderInfo orderInfo) {
        rabbitTemplate.convertAndSend(OrderRabbit.ORDER_CLOSE.exchange(), OrderRabbit.ORDER_CLOSE.routingKey(), orderInfo);
    }

    @Override
    public void sendOrderDeliver(OrderPackageKeyDTO orderPackageKey) {
        rabbitTemplate.convertAndSend(OrderRabbit.ORDER_DELIVER_GOODS.exchange(), OrderRabbit.ORDER_DELIVER_GOODS.routingKey(), orderPackageKey);
    }

    @Override
    public void sendDelayConfirmReceive(boolean checkItems, OrderPackageKeyDTO orderPackageKey, Long delayTime, Long orderShopId) {
        String orderNo = orderPackageKey.getOrderNo();
        if (
                checkItems
                        //检查是否包含其他未发货商品 如果包含则不发送自动延迟确认收货mq
                        && TenantShop.disable(
                        () -> shopOrderItemService.lambdaQuery()
                                .eq(ShopOrderItem::getOrderNo, orderNo)
                                .eq(ShopOrderItem::getShopId, orderShopId)
                                .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                                .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                                .exists()
                )
        ) {
            return;
        }

        //更新店铺订单的发货时间
        TenantShop.disable(() -> shopOrderService.lambdaUpdate()
                .eq(ShopOrder::getOrderNo, orderNo)
                .eq(ShopOrder::getShopId, orderShopId)
                .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("receivableTime", LocalDateTime.now().format(DatePattern.NORM_DATETIME_FORMATTER))))
                .update());
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_AUTO_CONFIRM_RECEIPT.exchange(),
                OrderRabbit.ORDER_AUTO_CONFIRM_RECEIPT.routingKey(),
                orderPackageKey,
                message -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, delayTime);
                    return message;
                }
        );
    }

    @Override
    public void sendDelayEvaluate(OrderPackageKeyDTO orderPackageKey, Long delayTime, Long orderShopId) {
        orderPackageKey.setShopId(orderShopId);
        //更新店铺订单的确认收货时间
        TenantShop.disable(() -> shopOrderService.lambdaUpdate()
                .eq(ShopOrder::getOrderNo, orderPackageKey.getOrderNo())
                .eq(ShopOrder::getShopId, orderPackageKey.getShopId())
                .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("receiveTime", LocalDateTime.now().format(DatePattern.NORM_DATETIME_FORMATTER))))
                .update());
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_AUTO_COMMENT.exchange(),
                OrderRabbit.ORDER_AUTO_COMMENT.routingKey(),
                orderPackageKey,
                message -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, delayTime);
                    return message;
                }

        );
    }

    @Override
    public void sendOrderCreateFailed(OrderInfo orderInfo) {
        rabbitTemplate.convertAndSend(OrderRabbit.ORDER_CREATE_FAILED.exchange(), OrderRabbit.ORDER_CREATE_FAILED.routingKey(), orderInfo);
    }

    @Override
    public void sendOrderCreated(OrderCreatedDTO orderCreated) {
        rabbitTemplate.convertAndSend(OrderRabbit.ORDER_CREATED.exchange(), OrderRabbit.ORDER_CREATED.routingKey(), orderCreated);
    }

    @Override
    public void sendDelayOrderAutoClose(String oderNo, Long delayTime) {
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_AUTO_PAID_TIMEOUT_CLOSE.exchange(),
                OrderRabbit.ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey(),
                oderNo,
                message -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, delayTime);
                    return message;
                }
        );
    }

    @Override
    public void sendOrderPaid(OrderPaidBroadcastDTO orderPaidBroadcast) {
        rabbitTemplate.convertAndSend(OrderRabbit.ORDER_PAID_BROADCAST.exchange(), OrderRabbit.ORDER_PAID_BROADCAST.routingKey(), orderPaidBroadcast);
    }

    @Override
    public void sendOrderFlush(OrderDetailInfoBO orderDetailInfo) {
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_CREATE.exchange(),
                OrderRabbit.ORDER_CREATE.routingKey(),
                orderDetailInfo
        );
    }

    @Override
    public void sendOrderEvaluate(OrderCompletedDTO orderCompleted) {
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_ACCOMPLISH.exchange(),
                OrderRabbit.ORDER_ACCOMPLISH.routingKey(),
                orderCompleted
        );
    }

    @Override
    public void sendAppletSubscribeMsg(SubscribeAppletMsgDTO subscribeApplet) {
        rabbitTemplate.convertAndSend(
                PigeonRabbit.PIGEON_APPLET_SUBSCRIBE.exchange(),
                PigeonRabbit.PIGEON_APPLET_SUBSCRIBE.routingKey(),
                subscribeApplet
        );
    }

    /**
     * 发送小程序订单发货消息
     *
     * @param orderPackageKey 订单
     */
    @Override
    public void sendMiniAppOrderDeliver(OrderPackageKeyDTO orderPackageKey) {
        //非快递配送，发货后发起微信物流录入
        if (!LogisticsType.EXPRESS.equals(orderPackageKey.getLogisticsType())) {
            orderPackageKey.setOrderUploadType(OrderUploadType.UPLOAD_SHIPPING_INFO);
            orderPackageKey.setDeliveryMode(DeliveryMode.UNIFIED_DELIVERY);
        } else {
            //快递配送，查询除当前明细外，是否还有未发货正在售后的明细
            List<ShopOrderItem> shopOrderItemList = TenantShop.disable(
                    () -> shopOrderItemService.lambdaQuery()
                            .eq(ShopOrderItem::getOrderNo, orderPackageKey.getOrderNo())
                            .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                            .eq(ShopOrderItem::getAfsStatus,
                                    CollUtil.newArrayList(
                                            AfsStatus.NONE, AfsStatus.REFUND_REQUEST, AfsStatus.REFUND_REJECT
                                    )
                            )
                            .ne(ShopOrderItem::getProductId, orderPackageKey.getPackageId())
                            .list()
            );

            orderPackageKey.setOrderUploadType(OrderUploadType.UPLOAD_COMBINED_SHIPPING_INFO);
            orderPackageKey.setDeliveryMode(DeliveryMode.SPLIT_DELIVERY);
            //判断是否最后一条未发货明细,不是直接结束
            if (!CollUtil.isEmpty(shopOrderItemList)) {
                return;
            }
        }
        rabbitTemplate.convertAndSend(
                OrderRabbit.MINI_APP_ORDER_DELIVER_GOODS.exchange(),
                OrderRabbit.MINI_APP_ORDER_DELIVER_GOODS.routingKey(), orderPackageKey
        );
    }

    @Override
    public void sendPrintOrder(OrderPrintDTO param) {
        rabbitTemplate.convertAndSend(
                OrderRabbit.ORDER_PRINT.exchange(),
                OrderRabbit.ORDER_PRINT.routingKey(),
                param
        );
    }




}
