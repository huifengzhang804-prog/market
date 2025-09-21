package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 交易类型枚举
 *
 * @author: WuDi
 * @date: 2022/10/10
 */
@Getter
@RequiredArgsConstructor
public enum TransactionType {


//--------------- 1.0.4 扩展 --------------------------------//
//--------------- 平台 收入 相关枚举 --------------------------------//

    /**
     * 会员余额充值
     */
    USER_BALANCE_RECHARGE(3),

    /**
     * 付费会员开通
     */
    PAID_MEMBER_OPEN(4),

    /**
     * 付费会员续费
     */
    PAID_MEMBER_RENEW(5),


    /**
     * 积分商品交易
     */
    INTEGRAL_GOODS_DEAL(7),

    /**
     * 系统服务  店铺支出 or 平台收入
     */
    SYSTEM_SERVICE(8),


//--------------- 1.0.4 扩展 --------------------------------//
//--------------- 平台 支出 or 店铺 收入 相关枚举 --------------------------------//
    /**
     * 会员抵扣
     */
    MEMBER_DISCOUNT(-1),


    /**
     * 会员运费优惠
     */
    MEMBER_LOGISTICS_DISCOUNT(-2),

    /**
     * 平台优惠券
     */
    PLATFORM_DISCOUNT_COUPON(-3),

//--------------- 2.0.1 扩展 --------------------------------//
    /**
     * 返利抵扣
     */
    REBATE_DEDUCTION(-4),


//--------------- 平台 支出 到用户 相关枚举 --------------------------------//
    /**
     * 返利赠送
     */
    REBATE_GIVE(-5),


//--------------- 1.0.4 扩展 --------------------------------//
//--------------- 店铺 收入 相关枚举 --------------------------------//

    /**
     * 订单支付
     */
    ORDER_PAID(1),

    /**
     * 订单运费
     */
    ORDER_FREIGHT(9),


//--------------- 1.0.4 扩展 --------------------------------//
//--------------- 店铺 支出 相关枚举 --------------------------------//


    /**
     * 分销佣金
     */
    DISTRIBUTE(6),


    /**
     * 采购订单
     */
    PURCHASE_ORDER(10),

    /**
     * 采购订单运费
     */
    PURCHASE_ORDER_FREIGHT(11),

    /**
     * 采购订单服务费
     */
    PURCHASE_ORDER_SERVICE_CHARGE(12),


    /**
     * 代销支出
     */
    CONSIGNMENT_DISBURSE(13),

    /**
     * 充值赠送
     */
    RECHARGE_GIFT(14),
    ;

    @EnumValue
    private final Integer code;


}

