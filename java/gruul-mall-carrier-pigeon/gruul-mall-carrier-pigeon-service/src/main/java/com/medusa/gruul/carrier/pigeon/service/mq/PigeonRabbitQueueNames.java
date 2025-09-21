package com.medusa.gruul.carrier.pigeon.service.mq;

/**
 * @author 张治保
 * date 2022/10/19
 */

public interface PigeonRabbitQueueNames {

    /**
     * 订单一支付 队列
     */
    String PIGEON_ORDER_PAID_BROADCAST_QUEUE = "pigeon.order.paid.broadcast";

    /**
     * 店铺信息修改
     */
    String PIGEON_SHOP_INFO_UPDATE_QUEUE = "pigeon.shop.info.update";

    /**
     * 小程序订阅消息
     */
    String PIGEON_APPLET_SUBSCRIBE_QUEUE = "pigeon.applet.subscribe";

    /**
     * 用户修改个人资料
     */
    String USER_DATA_UPDATE = "pigeon.user.data.update";

    /**
     * 发送短信 消息队列
     */
    String PIGEON_SEND_SMS = "pigeon.send.sms";
}
