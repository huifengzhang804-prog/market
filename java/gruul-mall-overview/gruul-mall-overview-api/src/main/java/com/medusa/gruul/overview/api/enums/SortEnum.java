package com.medusa.gruul.overview.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**排序枚举
 * @author jipeng
 * @since 2024/6/20
 */
@Getter
@RequiredArgsConstructor
public enum SortEnum {
  //对账单排序字段
  TIME_ASC("交易时间正序"),
  TIME_DESC("交易时间倒序"),
  AMOUNT_ASC("交易金额正序"),
  AMOUNT_DESC("交易金额倒序"),

  //提现工单排序字段
  APPLY_TIME_ASC("申请时间正序"),
  APPLY_TIME_DESC("申请时间倒序"),
  AUDIT_TIME_ASC("审核时间正序"),
  AUDIT_TIME_DESC("审核时间倒序"),
  WITHDRAW_AMOUNT_ASC("提现金额正序"),
  WITHDRAW_AMOUNT_DESC("提现金额倒序"),


  ;


  private final String context;


}
