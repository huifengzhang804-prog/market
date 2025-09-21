package com.medusa.gruul.addon.integral.model.constant;

/**
 * @author shishuqian
 * date 2023/2/1
 * time 15:55
 **/
public interface IntegralConstant {

    /**
     * 积分行为最大连续天数 连续签到/登录的最大天数
     */
    Integer INTEGRAL_BEHAVIOR_MAX_CONTINUE_DAYS = 7;

    /**
     * 积分商品最大购买数量
     */
    int INTEGRAL_PRODUCT_MAX_BUY_NUM = 1;

    /**
     * 积分订单前缀
     */
    String INTEGRAL_ORDER_NO_PREFIX = "JF23";

    /**
     * 积分商品库存字段
     */
    String INTEGRAL_PRODUCT_STOCK = "stock";

    /**
     * 积分商品真实销量字段
     * 订单key =>  gruul:mall:integral:order:create:{userId}:{orderNo}
     */
    String INTEGRAL_PRODUCT_REALITY_SALES_VOLUME = "realitySalesVolume";

    /**
     * 创建积分订单缓存前缀
     */
    String INTEGRAL_ORDER_CACHE_KEY = "gruul:mall:integral:order:create";

    /**
     * 积分商品更新库存的key
     */
    String INTEGRAL_PRODUCT_UPDATE_STOCK = "gruul:mall:integral:product:update:stock";

    /**
     * 积分商品真实销量 库存 增减 模板
     */
    String SQL_REALITY_SALES_VOLUME_INCREMENT_SQL_TEMPLATE = "reality_sales_volume = reality_sales_volume+{}";


    /**
     * 积分商品库存 增减 sql模板
     */
    String SQL_STOCK_INCREMENT_SQL_TEMPLATE = "stock = stock+{}";


    /**
     * 积分行为锁前缀
     */
    String INTEGRAL_BEHAVIOR_LOCK = "integral:behavior:lock";

    String INTEGRAL_ORDER_EDIT_LOCK_KEY = "lock:integral:order:edit";


    /**
     * 定时任务描述
     */
    String JOB_DESC = "定时清空用户积分";
    /**
     * handler
     */
    String EXECUTOR_HANDLER = "clearUserIntegralJob";
    /**
     * 定时任务负责人
     */
    String AUTHOR = "xxl";

    String CRON_TEMPLATE = "0 0 0 {} {}/{} ?";

    /**
     * 积分超时时间
     */
    String INTEGRAL_TIME_OUT = "addon:mall:integral:time";


    String INTEGRAL_ORDER_MINI_DELIVERY = "gruul:mall:integral:order:mini:delivery";
}
