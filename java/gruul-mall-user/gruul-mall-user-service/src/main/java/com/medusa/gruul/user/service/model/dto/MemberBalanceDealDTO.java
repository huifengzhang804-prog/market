package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import lombok.Data;

/**
 * 会员余额交易dto封装 用于
 *
 * @author xiaoq
 * @Description MemberBalanceDealDTO.java
 * @date 2022-10-09 13:38
 */
@Data
public class MemberBalanceDealDTO {

    /**
     * 用户支付金额
     */
    private Long payAmount;


    /**
     * 用户储值规则
     */
    private RuleJsonDTO ruleJson;


    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 用户id
     */
    private Long userId;


}
