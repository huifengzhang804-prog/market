package com.medusa.gruul.user.api.constant;

/**
 * @author xiaoq
 * description
 * date 2022-10-12 16:34
 */
public interface UserConstant {

    /**
     * 更新用户资料锁前缀
     */
    String USER_DATA_UPDATE_LOCK = "user:data:update:lock";

    /**
     * 用户余额分布式锁
     */
    String USER_BALANCE_KEY = "user:balance";


    /**
     * 用户新增/编辑 售后地址 分布式锁前缀
     */
    String USER_ADDRESS_UPDATE_LOCK = "user:address:update:lock";

    /**
     * 新增会员等级分布式锁
     */
    String NEW_FREE_MEMBER_CARD = "user:new:free:member:card";

    /**
     * 用户开通会员分布式锁
     */
    String USER_OPEN_PAID_MEMBER_CARD = "user:open:paid:member:card";

    /**
     * 用户总积分新增/减少 sql模板
     */
    String USER_ACCOUNT_INTEGRAL_TOTAL_SQL_TEMPLATE = "integral_total = integral_total + {}";

    /**
     * 用户积分分布式锁
     */
    String USER_INTEGRAL_TOTAL_KEY = "user:integral:total";

    /**
     * 店铺用户新增分布式锁
     */
    String USER_SHOP_SAVE = "shop:user:save";

    /**
     * 用户成长值分布式锁
     */
    String USER_GROWTH_VALUE_KEY = "user:growth:value";

    /**
     * 交易总金额新增/减少 sql模板
     */
    String USER_DEAL_TOTAL_MONEY_SQL_TEMPLATE = "deal_total_money = deal_total_money + {}";

    /**
     * 用户成长值新增/减少 sql模板
     */
    String USER_GROWTH_VALUE_SQL_TEMPLATE = "growth_value = growth_value + {}";
    /**
     * 奖励成长值的支付订单数
     */
    String USER_GROWTH_PAY_ORDER_COUNT_SQL_TEMPLATE = "growth_pay_order_count = growth_pay_order_count + {}";
    /**
     * 成长值的支付订单数重置
     */
    String USER_GROWTH_PAY_ORDER_COUNT_SQL_RESET_TEMPLATE = "growth_pay_order_count = 0";

    /**
     * 奖励成长值的实付金额
     */
    String USER_PAY_ORDER_MONEY_SQL_TEMPLATE = "growth_pay_order_money = growth_pay_order_money + {}";

    /**
     * 本店消费
     */
    String USER_SHOP_CONSUMPTION_SQL_TEMPLATE = "shop_consumption = shop_consumption + {}";

    /**
     * 会员流水前缀
     */
    String MEMBER_PURCHASE_HISTORY_NO_PREFIX = "hy";

    /**
     * 用户足迹保存分布式锁
     */
    String USER_FOOT_MARK_SAVE_LOCK = "user:foot:mark:save";

}
