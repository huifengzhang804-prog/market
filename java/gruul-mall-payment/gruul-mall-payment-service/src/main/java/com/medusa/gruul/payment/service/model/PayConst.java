package com.medusa.gruul.payment.service.model;

/**
 * 支付服务常量
 *
 * @author 张治保
 * date 2023/6/5
 */
public interface PayConst {

    /**
     * 支付缓存前缀
     */
    String PAYMENT_CACHE_PREFIX = "gruul:mall:payment:";

    /**
     * 微信服务商申请业务编号生成key
     */
    String WX_SERVICE_BUSINESS_NO_KEY = PAYMENT_CACHE_PREFIX + "wx:service:business:no";

    /**
     * app 场景下的 app 截图缓存 key
     */
    String APP_PICS_KEY = PAYMENT_CACHE_PREFIX + "app:pics";

    /**
     * 店铺特约商户数据 锁 key
     */
    String SHOP_MERCHANT_SUB_LOCK_KEY = PAYMENT_CACHE_PREFIX + "merchant:sub:lock";

    /**
     * 店铺订单售后申请 分布式锁key
     */
    String SHOP_ORDER_AFS_LOCK_KEY = PAYMENT_CACHE_PREFIX + "order:afs:lock";

    /**
     * 支付渠道分布式所 避免同一种支付渠道反复添加
     */
    String PAY_CHANNEL_LOCK_KEY = PAYMENT_CACHE_PREFIX + "channel:lock";

    /**
     * 支付配置缓存key
     */
    String PAY_CONFIG_CACHE_KEY = PAYMENT_CACHE_PREFIX + "config";

}
