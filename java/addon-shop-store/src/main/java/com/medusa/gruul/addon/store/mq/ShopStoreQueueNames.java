package com.medusa.gruul.addon.store.mq;

/**
 * 店铺门店队列
 *
 * @author xiaoq
 * @Description 店铺门店队列
 * @date 2023-03-15 21:41
 */
public interface ShopStoreQueueNames {
    /**
     * 店铺信息改动
     */
    String SHOP_INFO_UPDATE_QUEUE = "store.shop.info.update.queue";

    /**
     * 门店订单支付成功
     */
    String STORE_ORDER_PAY_SUCCEED_QUEUE = "store.order.pay.succeed.queue";


    /**
     * 门店订单完成
     */
    String STORE_ORDER_COMPLETE_QUEUE = "store.order.complete.queue";


    /**
     * 门店订单发货
     */
    String STORE_ORDER_DELIVER_GOODS_QUEUE = "store.order.deliver.goods.queue";
}
