package com.medusa.gruul.order.api.constant;

/**
 * @author 张治保
 * date 2022/6/9
 */
public interface OrderConstant {

    String ORDER_CACHE_PREFIX = "gruul:mall:order";
    /**
     * 创建订单缓存前缀
     * 订单key =>  gruul:mall:order:create:{userId}:{orderNo}
     */
    String ORDER_CACHE_KEY = ORDER_CACHE_PREFIX + ":create";

    /**
     * 小程序运力信息缓存key
     */
    String ORDER_MINI_DELIVER_CACHE_KEY = ORDER_CACHE_PREFIX + ":mini:delivery";
    /**
     * 修改订单的分布式锁信息
     * 完整key => lock:order:edit:{orderNo}
     */
    String ORDER_EDIT_LOCK_KEY = "lock:order:edit";

    /**
     * 操作订单包裹的锁
     */
    String ORDER_PACKAGE_LOCK = "order:package:lock";

    /**
     * 订单超时时间 缓存key
     */
    String ORDER_TIMEOUT_CACHE = ORDER_CACHE_PREFIX + ":timeout";

    /**
     * 订单超时时间分布式锁
     */
    String ORDER_TIMEOUT_LOCK = "order:timeout:lock";

    /**
     * 店铺交易信息缓存前缀
     */
    String ORDER_FORM_LOCK = "order:form:lock";


    /**
     * 店铺交易信息缓存前缀
     */
    String ORDER_FORM_CACHE = ORDER_CACHE_PREFIX + ":form";

    /**
     * 平台发货名称
     */
    String PLATFORM_NAME = "平台";

    /**
     * 取餐码缓存 key ,
     * 取餐码每日重置一次，从一开始 依次递增
     */
    String PICKUP_CODE_CACHE = ORDER_CACHE_PREFIX + ":pickup:code";

    /**
     * 平台备注remark key
     */
    String PLATFORM_REMARK_KEY = "platform";

    /**
     * 店铺备注remark key
     */
    String SHOP_REMARK_KEY = "shop";

    /**
     * 供应商备注remark key
     */
    String SUPPLIER_REMARK_KEY = "supplier";

    String ORDER_MIN_APP_DELIVER_CONFIRM="minAppDeliverConfirm";


}
