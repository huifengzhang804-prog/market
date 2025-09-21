package com.medusa.gruul.order.service.mq;

/**
 * @author 张治保
 * date 2022/6/14
 */
public interface OrderQueueNames {
    /**
     * 创建订单 后 把订单数据刷到数据库
     */
    String ORDER_CREATE_FLUSH_DATA2DB_QUEUE = "order.create.flush.data2db";

    /**
     * 超时未支付 系统自动关闭订单队列
     */
    String ORDER_AUTO_PAID_TIMEOUT_CLOSE_QUEUE = "order.auto.paidTimeout.close";

    /**
     * 超时未确认收货 系统自动确认收货
     */
    String ORDER_AUTO_CONFIRM_RECEIPT_QUEUE = "order.auto.confirmReceipt";

    /**
     * 超时未未评价 自动好评
     */
    String ORDER_AUTO_COMMENT_QUEUE = "order.auto.comment";


    /**
     * 订单支付 成功队列名 支付服务支付成功时的回调队列
     */
    String ORDER_PAID_CALLBACK_QUEUE = "order.paid.callback";

    /**
     * 小程序订单发货队列
     */
    String MINI_APP_ORDER_DELIVERY_QUEUE = "miniApp.order.delivery";
    /**
     * 小程序订单发货确认队列（客户确认收货）
     */
    String MINI_APP_ORDER_DELIVERY_CONFIRM_QUEUE = "miniApp.order.delivery.confirm";

    /**
     * 小程序订单退货微信物流录入 队列
     */
    String MINI_APP_ORDER_RETURN_GOODS_DELIVERY_QUEUE = "miniApp.order.return.goods.delivery";

    /**
     * 监听订单已支付 队列
     */
    String ORDER_PAID_QUEUE = "order.order.paid";

    /**
     * 打印订单小票 mq
     */
    String ORDER_PRINT_QUEUE = "order.print.ticket";
}
