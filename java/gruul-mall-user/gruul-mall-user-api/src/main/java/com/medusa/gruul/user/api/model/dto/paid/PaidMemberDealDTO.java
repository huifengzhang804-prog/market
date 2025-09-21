package com.medusa.gruul.user.api.model.dto.paid;

import com.medusa.gruul.common.model.enums.PayType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 付费会员交易DTO
 *
 * @author xiaoq
 * @Description PaidMemberDealDTO.java
 * @date 2022-11-17 15:54
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaidMemberDealDTO {

    /**
     * 付费会员id
     */
    private Long paidMemberId;

    /**
     *  付费会员级别
     */
    private Integer rankCode;

    /**
     * 用户支付金额
     */
    private Long payAmount;

    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 付费规则
     */
    private PaidRuleJsonDTO paidRuleJson;
}
