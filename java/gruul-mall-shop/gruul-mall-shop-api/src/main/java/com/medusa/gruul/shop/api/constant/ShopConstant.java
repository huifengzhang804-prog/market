package com.medusa.gruul.shop.api.constant;

/**
 * @author 张治保
 * date 2022/4/14
 */
public interface ShopConstant {

    String SHOP_CACHE_KEY_PREFIX = "gruul:mall:shop";
    /**
     * 商户id编号头
     */
    String SHOP_NO_KEY_HEAD = "GY";
    /**
     * 商户id编号生成key
     */
    String SHOP_NO_KEY = "ShopNoKey";


    /**
     * 获取店铺装修前缀
     */
    String DECORATION_PAGE_CACHE_KEY = SHOP_CACHE_KEY_PREFIX + ":decoration:page";

    /**
     * 获取店铺装修副本前缀
     */
    String DECORATION_PAGE_COPY_CACHE_KEY = DECORATION_PAGE_CACHE_KEY + ":copy";

    /**
     * 获取店铺装修模板副本前缀
     */
    String DECORATION_TEMPLATE_COPY_KEY = DECORATION_PAGE_CACHE_KEY + ":template:copy";

    /**
     * 店铺geo缓存 key
     */
    String SHOP_GEO = SHOP_CACHE_KEY_PREFIX + ":geo";

}
