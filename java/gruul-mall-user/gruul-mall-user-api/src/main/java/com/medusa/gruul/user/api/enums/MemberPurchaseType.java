package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.api.enums
 * @author:jipeng
 * @createTime:2024/1/22 17:56
 * @version:1.0
 */
@Getter
@RequiredArgsConstructor
public enum MemberPurchaseType {
  PAID_MEMBER_OPEN(1,"购买付费会员"),
  PAID_MEMBER_RENEW(2,"续费付费会员"),

  ;
  @EnumValue
  private final Integer value;

  private final String desc;
}
