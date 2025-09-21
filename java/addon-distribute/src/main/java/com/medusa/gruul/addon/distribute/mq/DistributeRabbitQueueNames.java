package com.medusa.gruul.addon.distribute.mq;

/**
 * @author 张治保
 * date 2022/11/15
 */
public interface DistributeRabbitQueueNames {

    /**
     * 监听商品删除消息队列
     */
    String GOODS_DELETE_QUEUE = "distribute.goods.delete";

    /**
     * 监听商品删除消息队列
     */
    String GOODS_UPDATE_QUEUE = "distribute.goods.update";

    /**
     * 监听商品状态修改
     */
    String GOODS_UPDATE_STATUS_QUEUE = "distribute.goods.update.status";

    /**
     * 店铺信息修改
     */
    String SHOP_UPDATE = "distribute.shop.update";

    /**
     * 订单已创建
     */
    String ORDER_CREATED = "distribute.order.created";

    /**
     * 订单已已关闭
     */
    String ORDER_CLOSED = "distribute.order.closed";

    /**
     * 订单已支付
     */
    String ORDER_PAID = "distribute.order.paid";

    /**
     * 订单评价消息队列
     */
    String ORDER_COMMENTED = "distribute.order.commented";

    /**
     * 提现工单审核未通过
     */
    String WITHDRAW_ORDER_FORBIDDEN = "distribute.withdraw.order.forbidden";

    /**
     * 用户修改个人资料
     */
    String USER_DATA_UPDATE = "distribute.user.data.update";

    /**
     * 商品sku金额修改
     */
    String UPDATE_PRODUCT_SKU_PRICE = "distribute.product.sku.price";

    /**
     * 商品名称修改
     */
    String PRODUCT_NAME_UPDATE = "distribute.product.name.update";
}
