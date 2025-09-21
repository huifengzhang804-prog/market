package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.common.model.enums.PayType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 支付历史表
 *
 * @author xiaoq
 * @Description PaymentHistory.java
 * @date 2022-08-31 09:22
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_payment_history")
@EqualsAndHashCode(callSuper = true)
public class PaymentHistory extends BaseEntity {



    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会员昵称
     */
    private String userName;

    /**
     * 会员头像
     */
    private String userHeadPortrait;
    /**
     * 商户隔离id
     */
    private Long shopId;

    /**
     * 金额
     */
    private Long money;

    /**
     * 当前金额
     */
    private Long currentMoney;

    /**
     * 交易类型
     */
    private DealType dealType;


    /**
     * 变化类型
     */
    private ChangeType changeType;

    /**
     * 交易标题
     */
    private String subject;

    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 备注信息
     */
    private String remark;

}
