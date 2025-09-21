package com.medusa.gruul.user.api.model.dto.integral;

import com.medusa.gruul.user.api.enums.integral.GainRuleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 积分获取规则DTO
 *
 * @author xiaoq
 * @Description IntegralGainRuleDTO.java
 * @date 2023-02-03 16:41
 */
@Getter
@Setter
@ToString
public class IntegralGainRuleDTO {

    /**
     * 获取积分规则参数
     */

    private RulesParameterDTO rulesParameter;

    /**
     * 获取积分规则类型
     */
    private GainRuleType gainRuleType;

    /**
     * 是否开启
     */
    private Boolean open;
}
