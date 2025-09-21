package com.medusa.gruul.addon.integral.model.vo;

import com.medusa.gruul.addon.integral.model.dto.IntegralGainRuleDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 积分规则vo
 *
 * @author xiaoq
 * @Description IntegralRulesVO.class
 * @date 2023-02-03 16:17
 */
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
