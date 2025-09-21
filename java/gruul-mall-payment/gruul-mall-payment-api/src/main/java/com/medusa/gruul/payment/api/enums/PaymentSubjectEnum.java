package com.medusa.gruul.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @projectName:gruul-mall-payment
 * @see:com.medusa.gruul.payment.api.enums
 * @author:jipeng
 * @createTime:2024/1/27 14:18
 * @version:1.0
 */
@Getter
@AllArgsConstructor
public enum PaymentSubjectEnum {

  USER_OPENS_PAID_MEMBERSHIP("用户开通付费会员");
  String subject;
}
