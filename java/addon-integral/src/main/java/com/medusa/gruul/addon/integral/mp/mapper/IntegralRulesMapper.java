package com.medusa.gruul.addon.integral.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import org.springframework.data.repository.query.Param;

/**
 * 积分规则持久层
 *
 * @author xiaoq
 * @Description 积分规则持久层
 * @date 2023-02-03 16:50
 */
public interface IntegralRulesMapper extends BaseMapper<IntegralRules> {
    /**
     * 获取积分规则
     *
     * @return 积分规则VO
     */
    IntegralRulesVO getIntegralRule();

    IntegralRules getIntegralRuleByRuleType(@Param("query") GainRuleType gainRuleType);
}
