package com.medusa.gruul.addon.integral.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分规则DTO
 *
 * @author xiaoq
 * @Description IntegralRulesDTO.class
 * @date 2023-02-03 16:19
 */
@Getter
@Setter
@ToString
public class IntegralRulesDTO implements BaseDTO {
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

    /**
     * 积分获取规则dto
     */
    private List<IntegralGainRuleDTO> integralGainRule;

    @Override
    public void validParam() {
            if (CollUtil.isNotEmpty(integralGainRule)){
                List<IntegralGainRuleDTO> ruleDTOList = integralGainRule.stream().filter(IntegralGainRuleDTO::getOpen).filter(integralGainRule -> integralGainRule.getGainRuleType() == GainRuleType.CONSUME).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(ruleDTOList)){
                    IntegralGainRuleDTO integralGainRule = ruleDTOList.get(0);
                    RulesParameterDTO rulesParameter = integralGainRule.getRulesParameter();
                    List<ConsumeJsonDTO> consumeJsons = rulesParameter.getConsumeJson().stream().filter(ConsumeJsonDTO::getIsSelected).toList();
                    if (CollUtil.isEmpty(consumeJsons)){
                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "积分消费所得规则不全");
                    }
                    ConsumeJsonDTO consumeJson = consumeJsons.get(0);
                    if (consumeJson.getConsumeGrowthValueType() == null
                            || consumeJson.getOrderQuantityAndAmount() == null
                            || consumeJson.getPresentedGrowthValue() ==null){
                       throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "积分消费所得规则不全");
                    }
                    if (consumeJson.getOrderQuantityAndAmount() <= 0){
                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "订单数量与金额不能小于0");
                    }
                }
            }
    }
}
