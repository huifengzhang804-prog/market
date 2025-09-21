package com.medusa.gruul.addon.store.constant;

/**
 * @author xiaoq
 * @Description 店铺门店常量
 * @date 2023-03-07 18:25
 */
public interface ShopStoreConstant {

    /**
     * 店铺店员新增分布式lock
     */
    String SHOP_STORE_ISSUE_LOCK = "shop:assistant:issue:lock";

    /**
     * 店铺门店订单核销分布式lock
     */
    String SHOP_STORE_ORDER_VERIFICATION_LOCK = "shop:store:order:verification:lock";
}
