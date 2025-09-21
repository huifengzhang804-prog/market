package com.medusa.gruul.addon.integral.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;

/**
 * 积分规则数据层
 *
 * @author xiaoq
 * @Description IIntegralRulesService
 * @date 2023-02-03 16:47
 */
public interface IIntegralRulesService extends IService<IntegralRules> {
    /**
     * 获取积分规则
     *
     * @return 积分规则VO
     */
    IntegralRulesVO getIntegralRule();

    IntegralRules getIntegralRuleByRuleType(GainRuleType gainRuleType);

}
