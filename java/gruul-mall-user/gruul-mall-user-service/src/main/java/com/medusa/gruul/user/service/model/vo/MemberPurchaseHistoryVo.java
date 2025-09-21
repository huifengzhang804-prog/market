package com.medusa.gruul.user.service.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.user.api.enums.MemberPurchaseType;
import com.medusa.gruul.user.api.enums.paid.EffectiveDurationType;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.model.vo
 * @author:jipeng
 * @createTime:2024/1/17 15:10
 * @version:1.0
 */
@Data
public class MemberPurchaseHistoryVo {

  private Long id;
  /**
   * 订单号
   */

  private String no;
  /**
   * 购买会员的用户id
   */

  private Long userId;

  /**
   * 用户昵称
   */

  private String userNickName;

  /**
   * 用户手机号
   */

  private String userPhone;

  /**
   * 会员等级id
   */

  private Long memberId;
  /**
   *  付费会员级别
   */

  private Integer rankCode;
  /**
   * 有效期类型
   */
  private EffectiveDurationType effectiveDurationType;
  /**
   * 有效期类型字符串
   */
  private String effectiveStr;
  /**
   * 支付金额
   */

  private Long payAmount;

  /**
   * 支付方式
   */

  private PayType payType;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime createTime;

  private MemberPurchaseType type;

  private String memberPurchaseTypeStr;

  /**
   * 到期时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime expireTime;
}
