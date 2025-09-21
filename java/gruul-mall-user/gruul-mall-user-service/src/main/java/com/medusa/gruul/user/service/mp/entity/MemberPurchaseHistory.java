package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.enums.MemberPurchaseType;
import com.medusa.gruul.user.api.enums.paid.EffectiveDurationType;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**

 * @author jipeng
 * @Description 会员流水 Model
 * @date 2022-11-15 15:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_member_purchase_history", autoResultMap = true)
public class MemberPurchaseHistory extends BaseEntity {

  /**
   * 订单号
   */
  @TableField("no")
  private String no;
  /**
   * 购买会员的用户id
   */
  @TableField("user_id")
  private Long userId;

  /**
   * 用户昵称
   */
  @TableField("user_nick_name")
  private String userNickName;

  /**
   * 用户手机号
   */
  @TableField("user_phone")
  private String userPhone;

  /**
   * 会员等级id
   */
  @TableField("member_id")
  private Long memberId;
  /**
   *  付费会员级别
   */
  @TableField("rank_code")
  private Integer rankCode;
  /**
   * 有效期
   */
  @TableField("effective_duration_type")
  private EffectiveDurationType effectiveDurationType;
  /**
   * 支付金额
   */
  @TableField("pay_amount")
  private Long payAmount;

  /**
   * 支付方式
   */
  @TableField("pay_type")
  private PayType payType;

  /**
   * 到期时间
   */
  @TableField("expire_time")
  private LocalDateTime expireTime;
  /**
   * 订单编号
   */
  @TableField("order_no")
  private String orderNo;
  /**
   * 会员购买类型
   */
  @TableField("type")
  private MemberPurchaseType type;




}
