package com.medusa.gruul.addon.platform.mq;

/**
 * 平台队列名称
 *
 * @author xiaoq
 * @Description PlatformQueueNames.java
 * @date 2023-09-06 15:30
 */
public interface PlatformQueueNames {
    String SHOP_CHANGE_QUEUE = "platform.shop.change.queue";
    /**
     * 店铺签约分类自定义折扣率发生变更的监听队列
     */
    String SHOP_SIGNING_CATEGORY__CUSTOM_DEDUCTION_RATIO_CHANGE_QUEUE = "platform.shop.signing.category.custom.deduction.ratio.change.queue";
}
