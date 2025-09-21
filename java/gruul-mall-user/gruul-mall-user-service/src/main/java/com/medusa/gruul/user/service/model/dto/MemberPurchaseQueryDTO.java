package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.mp.entity.MemberPurchaseHistory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 会员流水查询DTO
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.api.model.vo
 * @author:jipeng
 * @createTime:2024/1/17 14:54
 * @version:1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPurchaseQueryDTO extends Page<MemberPurchaseHistory> {

  /**
   * 订单号
   */
  private String no;
  /**
   * 手机号
   */
  private String userPhone;
  /**
   * 昵称
   */
  private String nickName;
  /**
   * 会员等级
   */
  private String level;
  /**
   * 购买开始时间
   */
  private LocalDateTime buyStartTime;
  /**
   * 购买结束时间
   */

  private LocalDateTime buyEndTime;

  /**
   *失效开始时间
   */
  private LocalDateTime expireStartTime;
  /**
   * 失效结束时间
   */
  private LocalDateTime expireEndTime;
  /**
   * 导出的ids
   */
  private List<Long> exportIds;
}
