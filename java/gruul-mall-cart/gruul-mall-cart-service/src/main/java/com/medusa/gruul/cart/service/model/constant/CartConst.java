package com.medusa.gruul.cart.service.model.constant;


/**
 * @author 张治保
 * date 2022/5/16
 */
public interface CartConst {
    /**
     * 未知店铺店铺名
     */
    String UNKNOWN_SHOP_NAME = "未知店铺";
    /**
     * 我的购物车缓存key
     *gruul:mall:cart:my:cart:{userId}
     */
    String MY_CART_CACHE_KEY = "gruul:mall:cart:my:cart";
    /**
     * 店铺名称缓存前缀
     * shop:name:get:{shopId}
     */
    String SHOP_NAME_CACHE_KEY = "shop:name:get";

    /**
     * 店铺加购数计数
     * gruul:mall:cart:shop:count:{shopId}
     */
    String SHOP_COUNT_CART_KEY = "gruul:mall:cart:shop:count";
}
