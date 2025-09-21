package com.medusa.gruul.addon.member.model.dto;

import com.medusa.gruul.user.api.model.dto.RelevancyRightsDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 付费会员DTO
 *
 * @author xiaoq
 * @Description PaidMemberDTO.java
 * @date 2022-11-15 11:09
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaidMemberDTO {

    private Long id;

    /**
     * 付费会员名称
     */
    private String paidMemberName;


    /**
     * 付费规则
     */
    private List<PaidRuleJsonDTO> paidRuleJson;


    /**
     * 关联权益
     */
    private List<RelevancyRightsDTO> relevancyRightsList;
}
