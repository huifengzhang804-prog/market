package com.medusa.gruul.order.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * 订单mq配置
 *
 * @author 张治保
 * date 2022/6/9
 */
@RequiredArgsConstructor
public enum OrderRabbit implements RabbitParent {

    /**
     * 创建订单
     */
    ORDER_CREATE("order.create"),
    /**
     * 订单创建失败 通知
     */
    ORDER_CREATE_FAILED("order.create.failed"),
    /**
     * 订单创建完成
     */
    ORDER_CREATED("order.created"),
    /**
     * 订单关闭
     */
    ORDER_CLOSE("order.close"),
    /**
     * 订单支付超时 自动关闭
     */
    ORDER_AUTO_PAID_TIMEOUT_CLOSE("order.auto.paidTimeOutCLose"),

    /**
     * 系统自动确认收货
     */
    ORDER_AUTO_CONFIRM_RECEIPT("order.auto.confirmReceipt"),

    /**
     * 系统自动好评
     */
    ORDER_AUTO_COMMENT("order.auto.comment"),

    /**
     * 订单支付成功 mq的routing key
     */
    ORDER_PAID_CALLBACK("order.paid.callback"),

    /**
     * 订单支付成功 广播
     */
    ORDER_PAID_BROADCAST("order.paid.broadcast"),

    /**
     * 订单完成 -- 指评价完成
     */
    ORDER_ACCOMPLISH("order.accomplish"),

    /**
     * 订单发货
     */
    ORDER_DELIVER_GOODS("order.deliver.goods"),

    /**
     * 小程序订单发货录入
     */
    MINI_APP_ORDER_DELIVER_GOODS("mini.app.order.deliver.goods"),
    /**
     * 小程序订单发货确认
     */
    MINI_APP_ORDER_DELIVERY_CONFIRM("min.app.order.delivery.confirm"),

    /**
     * 小程序订单重新发货录入
     */
    MINI_APP_ORDER_RETURN_GOODS("mini.app.order.return.goods"),

    /**
     * 打印店铺订单小票 （暂只支持同城、门店配送小票）
     */
    ORDER_PRINT("order.print"),

    /**
     * 发布打印消息
     */
    PRINT_PUBLISH("order.print.publish"),
    ;
    public static final String EXCHANGE = "order.direct";
    /**
     * 路由key
     */
    private final String routingKey;

    @Override
    public String exchange() {
        return OrderRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
