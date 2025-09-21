package com.medusa.gruul.addon.rebate.model;

public interface RebateConstant {

    /**
     * 消费返利配置缓存key
     */
    String REBATE_CONFIG_CACHE_KEY = "addon:rebate:config";

    /**
     * 创建订单
     */
    String REBATE_ORDER_CREATED_LOCK_KEY = "addon:rebate:order:created";

    /**
     * 修改用户返利余额分布式锁
     */
    String REBATE_BALANCE_LOCK_KEY = "addon:rebate:balance";

    /**
     * 待结算返利
     */
    String UNSETTLED_REBATE_SQL_TEMPLATE = "unsettled_rebate = unsettled_rebate + {}";

    /***
     * 已失效返利
     */
    String EXPIRED_REBATE_SQL_TEMPLATE = "expired_rebate = expired_rebate + {}";

    /**
     * 返利余额
     */
    String REBATE_BALANCE_SQL_TEMPLATE = "rebate_balance = rebate_balance + {}";

    /**
     * 累计返利
     */
    String ACCUMULATED_REBATE_SQL_TEMPLATE = "accumulated_rebate = accumulated_rebate + {}";
}
