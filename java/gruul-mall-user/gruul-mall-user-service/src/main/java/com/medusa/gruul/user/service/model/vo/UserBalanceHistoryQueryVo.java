package com.medusa.gruul.user.service.model.vo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @author:jipeng
 * @createTime:2024/1/19 16:43
 * @version:1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBalanceHistoryQueryVo extends Page<UserBalanceHistoryQueryVo> {

  /**
   * 流水编号
   */
  private String no;

  /**
   * 用户昵称
   */
  private String userNickName;


  /**
   * 用户手机号
   */
  private String userPhone;

  /**
   * 操作类型
   */
  private BalanceHistoryOperatorType operatorType;
  /**
   * 关联订单
   */
  private String orderNo;
  /**
   * 操作开始时间
   */

  private LocalDateTime operatorStartTime;
  /**
   * 操作结束时间
   */
  private LocalDateTime operatorEndTime;

  private List<Long> exportIds;




}
