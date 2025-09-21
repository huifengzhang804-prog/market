package com.medusa.gruul.user.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaoq
 * @Description
 * @date 2022-09-05 10:12
 */
@RequiredArgsConstructor
public enum UserRabbit implements RabbitParent {

    /**
     * 用户余额发生变化
     */
    USER_BALANCE_CHANGE("user.balance.change"),


    /**
     * 用户充值余额完成
     */
    USER_RECHARGE_BALANCE_OK("user.recharge.balance.ok"),

    /**
     * 用户交易总额
     */
    USER_TRADE_AMOUNT("user.trade.amount"),

    /**
     * 用户积分变化
     */
    USER_INTEGRAL_CHANGE("user_integral_change"),

    /**
     * 用户足迹新增
     * footprint
     */
    USER_FOOTPRINT_ADD("user.footprint.add"),


    /**
     * 用户虚拟发货 (会员开通|续费)
     */
    USER_VIRTUAL_DELIVER("user.virtual.deliver"),

    /**
     * 用户储值虚拟发货(余额储值)
     */
    USER_BALANCE_VIRTUAL_DELIVER("user.balance.virtual.deliver"),
    ;
    private final String routingKey;

    public static final String EXCHANGE = "user.direct";

    @Override
    public String exchange() {
        return UserRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
