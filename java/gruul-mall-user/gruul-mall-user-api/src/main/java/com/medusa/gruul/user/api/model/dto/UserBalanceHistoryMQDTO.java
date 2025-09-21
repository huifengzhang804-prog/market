package com.medusa.gruul.user.api.model.dto;

import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.api.model.dto
 * @author:jipeng
 * @createTime:2024/1/19 14:09
 * @version:1.0
 */
@Data
@Accessors(chain = true)
public class UserBalanceHistoryMQDTO {

  /**
   * 用户id
   */
  private Long userId;
  /**
   * 操作类型
   */
  private BalanceHistoryOperatorType operatorType;

  /**
   * 变动金额
   */
  private Long amount;

  /**
   * 变动类型
   */
  private ChangeType changeType;

  /**
   * 订单号
   */
  private String orderNo;

  /**
   *
   * 操作用户id 如果是系统操作id填充0
   */
  private Long operatorUserId;

}
