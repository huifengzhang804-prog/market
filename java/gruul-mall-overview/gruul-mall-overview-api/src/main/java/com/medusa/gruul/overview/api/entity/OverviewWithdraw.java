package com.medusa.gruul.overview.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 提现工单
 *
 * @author 张治保
 * @since 2022-11-19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_overview_withdraw", autoResultMap = true)
public class OverviewWithdraw extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息id 用作mq消费的幂等性校验
     */
    private String msgId;

    /**
     * 来源类型
     */
    private WithdrawSourceType sourceType;

    /**
     * 提现工单号
     */
    private String no;

    /**
     * 是否已走线下打款
     */
    private Boolean offline;

    /**
     * 来源类型
     */
    @TableField("`status`")
    private WithdrawOrderStatus status;

    /**
     * 提现者类型
     */
    private OwnerType ownerType;

    /**
     * 用户id/店铺id 由提现者类型决定
     */
    private Long ownerId;

    /**
     * 联系方式
     */
    private String contract;

    /**
     * 提现方式
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private DrawTypeModel drawType;

    /**
     * 关闭原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 交易流水号 支付宝/微信 线上交易时存在此字段
     */
    private String tradeNo;

    /**
     * 交易时间 支付宝/微信 线上交易时存在此字段
     */
    private String tradeTime;
    /**
     * 审核时间
     */
    @TableField(value = "audit_time")
    private LocalDateTime auditTime;
    /**
     * 审核人
     */
    @TableField(value = "audit_user_id")
    private Long auditUserId;
    /**
     * 支付凭证
     */
    @TableField(value = "payment_voucher")
    private String paymentVoucher;
    /**
     * 三方转账失败的原因
     */
    @TableField(value = "fail_reason")
    private String failReason;

    /**
     * 姓名/店铺名称
     */
    @TableField(exist = false)
    private String ownerName;

    /**
     * logo/头像
     */
    @TableField(exist = false)
    private String ownerAvatar;

    /**
     * 审核用户名称
     */
    @TableField(exist = false)
    private String auditUserName;
    /**
     * 审核用户手机号
     */
    @TableField(value = "audit_user_phone")
    private String auditUserPhone;

    /**
     * 申请人ID
     */
    @TableField(value = "apply_user_id")
    private Long applyUserId;

    @TableField(exist = false)
    private String applyUserNickName;

    /**
     * 申请人手机号
     */
    @TableField(value = "apply_user_phone")
    private String applyUserPhone;
}
