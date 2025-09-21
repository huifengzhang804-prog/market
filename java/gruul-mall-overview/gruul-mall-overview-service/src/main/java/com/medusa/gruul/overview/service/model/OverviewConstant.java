package com.medusa.gruul.overview.service.model;

/**
 * @author 张治保
 * date 2022/11/21
 */
public interface OverviewConstant {

    /**
     * 提现工单前缀
     */
    String WITHDRAW_NO_PREFIX = "WD";

    /**
     * 生成提现工单单号的key
     */
    String WITHDRAW_NO_KEY = "overview:withdraw";

    /**
     * 提现工单消息分布式锁前缀
     */
    String WITHDRAW_MSG_LOCK_KEY = "overview:withdraw:msg:lock";

    /**
     * 修改提现工单的分布式锁
     */
    String WITHDRAW_ORDER_UPDATE_LOCK_KEY = "overview:withdraw:order:update:lock";

    /**
     * 店铺信息缓存前缀
     */
    String OVERVIEW_SHOP_CACHE_KEY = "gruul:mall:overview:overview:shop:cache";

    /**
     * 更新店铺信息分布式锁
     */
    String OVERVIEW_UPDATE_SHOP_LOCK_KEY = "overview:shop:update:lock";


    String OVERVIEW_CONSUMER_CACHE_KEY = "gruul:mall:overview:consumer:cache";

    /**
     * 移动端用户信息分部锁(返利,分销)
     */
    String OVERVIEW_SAVE_CONSUMER_LOCK_KEY = "overview:consumer:save:lock";

    /**
     * 新增店铺余额数据 分布式锁
     */
    String NEW_SHOP_BALANCE_LOCK = "overview:shop:balance:new";

    /**
     * 更新店铺未提现余额分布式锁
     */
    String SHOP_BALANCE_UNDRAWN_UPDATE_LOCK = "overview:shop:update:balance:undrawn:lock";

    /**
     * 更新店铺未结算余额分布式锁
     */
    String SHOP_BALANCE_UNCOMPLETED_UPDATE_LOCK = "overview:shop:update:balance:uncompleted:lock";

    /**
     * 更新未提现余额
     */
    String UPDATE_BALANCE_UNDRAWN_SQL_TEMPLATE = "undrawn=undrawn+{}";

    /**
     * 更新总额
     */
    String UPDATE_BALANCE_TOTAL_SQL_TEMPLATE = "total=total+{}";

    /**
     * 更新未结算余额
     */
    String UPDATE_BALANCE_UNCOMPLETED_SQL_TEMPLATE = "uncompleted=uncompleted+{}";


    /**
     * 对账单更新
     */
    String OVERVIEW_STATEMENT_NEWLY = "overview:statement_newly";


    /**
     * 修改用户佣金分布式锁
     */
    String OVERVIEW_ACCOUNTS_LOCK_KEY = "overview:accounts:lock";
}

