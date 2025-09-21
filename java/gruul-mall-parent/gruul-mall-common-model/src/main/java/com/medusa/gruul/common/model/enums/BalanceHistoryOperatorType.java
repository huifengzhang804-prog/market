package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.api.enums
 * @author:jipeng
 * @createTime:2024/1/19 13:20
 * @version:1.0
 */
@Getter
@RequiredArgsConstructor
public enum BalanceHistoryOperatorType {

  SYSTEM_GIFT(1,"充值赠送"),
  USER_RECHARGE(2,"用户充值"),
  SYSTEM_RECHARGE(3,"系统充值"),
  SYSTEM_DEDUCTION(4,"系统扣除"),
  SHOPPING_CONSUMPTION(5,"购物消费"),
  PURCHASE_MEMBERSHIP(6,"购买会员"),
  RENEWAL_MEMBERSHIP(7,"续费会员"),
  UPGRADE_MEMBERSHIP(8,"升级会员"),
  REFUND_SUCCESSFUL(9,"退款成功")



  ;

  @EnumValue
  private final Integer value;
  private final String msg;

}
