package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 用户储值流水
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.mp.entity
 * @author:jipeng
 * @createTime:2024/1/19 13:05
 * @version:1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_user_balance_history", autoResultMap = true)
public class UserBalanceHistory extends BaseEntity {

  /**
   * 流水编号
   */
  @TableField(value = "no")
  private Long no;
  /**
   * 用户id
   */
  @TableField(value = "user_id")
  private Long userId;
  /**
   * 用户昵称
   */
  @TableField(value = "user_nick_name")
  private String userNickName;
  /**
   * 手机号
   */
  @TableField(value = "user_phone")
  private String userPhone;

  /**
   * 操作类型
   */
  @TableField(value = "operator_type")
  private BalanceHistoryOperatorType operatorType;

  /**
   * 变动金额
   */
  @TableField(value = "amount")
  private Long amount;
  /**
   * 期后金额
   */
  @TableField(value = "after_amount")
  private Long afterAmount;
  /**
   * 变动类型
   */
  @TableField(value = "change_type")
  private ChangeType changeType;

  /**
   * 订单号
   */
  @TableField(value = "order_no")
  private String orderNo;
  /**
   * 备注
   */
  @TableField(value = "remark")
  private String remark;
  /**
   *
   * 操作用户id 如果是系统操作id填充0
   */
  @TableField(value = "operator_user_id")
  private Long operatorUserId;

}
