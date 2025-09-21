package com.medusa.gruul.user.api.model.vo;

import com.medusa.gruul.user.api.model.dto.integral.IntegralGainRuleDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author shishuqian
 * date 2023/2/14
 * time 14:26
 **/
@Getter
@Setter
@ToString
public class IntegralRulesVO {

    /**
     * 积分使用规则
     */
    private String useRule;

    /**
     * 积分有效期
     */
    private Integer indate;

    /**
     * 积分值信息
     */
    private String ruleInfo;

    private List<IntegralGainRuleDTO> integralGainRule;

}
