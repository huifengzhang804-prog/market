package com.medusa.gruul.addon.matching.treasure.mq;

/**
 * 搭配宝队列
 */
public interface MatchingTreasureQueueNames {


    /**
     * 商品更新
     */
    String PRODUCT_UPDATE = "matching.treasure.product.update";


    /**
     * 商品删除
     */
    String PRODUCT_DELETE = "matching.treasure.product.delete";

    /**
     * 商品下架
     */
    String PRODUCT_SELL_OFF = "matching.treasure.product.sell.off";

    /**
     * 套餐活动支付信息
     */
    String MATCHING_TREASURE_PAYMENT_INFO = "matching.treasure.payment.info";

    /**
     * 套餐活动支付退款信息
     */
    String MATCHING_TREASURE_REFUND_INFO = "matching.treasure.refund.info";

    /**
     * 套餐活动变更（已结束或正在进行）
     */
    String MATCHING_TREASURE_UPDATE_SETMEAL_STATUS = "matching.treasure.updateSetMealStatus";
}
