package com.medusa.gruul.addon.member.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 支付dto
 *
 * @author xiaoq
 * @Description PayDTO.java
 * @date 2022-11-17 14:51
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PayDTO {

    /**
     * 付费会员id
     */
    @NotNull
    private Long id;

    /**
     * 支付渠道
     */
    @NotNull
    private PayType payType;

    /**
     * 支付金额
     */
    @NotNull
    private Long payAmount;

    /**
     * 付费规则id
     */
    @NotNull
    private Long paidRuleId;


}
