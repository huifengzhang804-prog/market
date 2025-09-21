package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 用户储值流水对外DTO
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.mp.entity
 * @author:jipeng
 * @createTime:2024/1/19 13:05
 * @version:1.0
 */
@Data
@Accessors(chain = true)
public class UserBalanceHistoryDTO extends BaseEntity {

  private Long id;

  private List<Long> ids;
  /**
   * 流水编号
   */

  private Long no;
  /**
   * 用户id
   */

  private Long userId;
  /**
   * 用户昵称
   */

  private String userNickName;
  /**
   * 手机号
   */

  private String userPhone;

  /**
   * 操作类型
   */

  private BalanceHistoryOperatorType operatorType;

  private String operatorTypeStr;

  /**
   * 变动金额
   */

  private Long amount;
  /**
   * 变动金额字符串
   */
  private String amountStr;
  /**
   * 期后金额
   */

  private Long afterAmount;

  private String afterAmountStr;
  /**
   * 变动类型
   */
  private ChangeType changeType;

  /**
   * 订单号
   */

  private String orderNo;
  /**
   * 备注
   */

  private String remark;
  /**
   *
   * 操作用户id 如果是系统操作id填充0
   */

  private Long operatorUserId;
  /**
   * 操作用户昵称
   */

  private String operatorUserNickName;

}
