package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.dto.IntegralRulesDTO;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;

/**
 * 管理端积分规则数据层
 *
 * @author xiaoq
 * @Description 管理端积分规则数据层
 * @date 2023-02-03 16:44
 */
public interface ManageIntegralRuleService {
    /**
     * 积分规则保存
     *
     * @param integralRules 积分规则信息
     */
    void saveIntegralRules(IntegralRulesDTO integralRules);

    /**
     * 积分规则修改
     *
     * @param integralRules 积分规则修改
     */
    void updateIntegralRules(IntegralRulesDTO integralRules);

    /**
     * 积分规则 获取
     *
     * @return 积分规则VO
     */
    IntegralRulesVO getIntegralRule();

}
