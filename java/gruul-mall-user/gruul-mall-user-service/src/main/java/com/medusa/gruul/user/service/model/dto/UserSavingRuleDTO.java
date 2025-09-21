package com.medusa.gruul.user.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *  用户储值规则DTO
 *
 * @author xiaoq
 * @Description UserSavingRuleDTO.java
 * @date 2022-09-01 14:55
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserSavingRuleDTO {

    private Long id;

    /**
     * 优惠状态 0无优惠 1有优惠
     */
    private Boolean discountsState;


    /**
     * 存储规则json
     */
    private List<RuleJsonDTO> ruleJson;
}
