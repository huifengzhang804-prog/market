package com.medusa.gruul.order.service.mq.service;

import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;

/**
 * @author 张治保
 * date 2023/6/29
 */
public interface OrderRabbitService {

    /**
     * 发送订单已关闭 mq
     *
     * @param orderInfo 订单信息
     */
    void sendOrderClose(OrderInfo orderInfo);

    /**
     * 发送已发货 mq 消息
     *
     * @param orderPackageKey 店铺订单关键 key
     */
    void sendOrderDeliver(OrderPackageKeyDTO orderPackageKey);


    /**
     * 会检查是否是最后一个发货的商品 如果是则更新店铺订单的总发货时间
     * 发送延迟确认收货 mq
     *
     * @param checkItems      是否检查是否包含其他未发货商品 如果包含则不发送自动延迟确认收货mq
     * @param orderPackageKey 店铺订单关键 key
     * @param delayTime       延迟时间 单位豪秒
     * @param orderShopId     订单shopId
     */
    void sendDelayConfirmReceive(boolean checkItems, OrderPackageKeyDTO orderPackageKey, Long delayTime, Long orderShopId);

    /**
     * 发送延迟评价 mq 需要更新店铺的确认收货时间
     *
     * @param orderPackageKey 店铺订单关键 key
     * @param delayTime       延迟时间 单位豪秒
     * @param orderShopId     订单shopId
     */
    void sendDelayEvaluate(OrderPackageKeyDTO orderPackageKey, Long delayTime, Long orderShopId);

    /**
     * 发送订单创建失败 mq
     *
     * @param orderInfo 订单信息
     */
    void sendOrderCreateFailed(OrderInfo orderInfo);

    /**
     * 发送订单已创建 mq
     *
     * @param orderCreated 订单创建信息
     */
    void sendOrderCreated(OrderCreatedDTO orderCreated);

    /**
     * 发送订单自动关闭 延迟 mq
     *
     * @param orderNo   订单号
     * @param delayTime 延迟时间 单位豪秒
     */
    void sendDelayOrderAutoClose(String orderNo, Long delayTime);

    /**
     * 发送订单已支付 mq
     *
     * @param orderPaidBroadcast 订单支付信息
     */
    void sendOrderPaid(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 发送订单入库 mq
     *
     * @param orderDetailInfo 订单详情
     */
    void sendOrderFlush(OrderDetailInfoBO orderDetailInfo);


    /**
     * 发送订单已评价 mq
     *
     * @param orderCompleted 订单完成信息
     */
    void sendOrderEvaluate(OrderCompletedDTO orderCompleted);

    /**
     * 发送小程序订阅消息
     *
     * @param subscribeApplet 订阅消息
     */
    void sendAppletSubscribeMsg(SubscribeAppletMsgDTO subscribeApplet);

    /**
     * 发送小程序订单发货消息
     *
     * @param orderPackageKey 订单
     */
    void sendMiniAppOrderDeliver(OrderPackageKeyDTO orderPackageKey);


    /**
     * 发送打印订单消息
     */
    void sendPrintOrder(OrderPrintDTO param);



}
