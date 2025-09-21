package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralRules;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralRulesMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralRulesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 积分规则数据实现层
 *
 * @author xiaoq
 * @Description 积分规则数据实现层
 * @date 2023-02-03 16:49
 */
@Service
@RequiredArgsConstructor
public class IntegralRulesServiceImpl extends ServiceImpl<IntegralRulesMapper, IntegralRules> implements IIntegralRulesService {
    private final IntegralRulesMapper integralRulesMapper;

    @Override
    public IntegralRulesVO getIntegralRule() {
        return integralRulesMapper.getIntegralRule();
    }


    @Override
    public IntegralRules getIntegralRuleByRuleType(GainRuleType gainRuleType) {
        return integralRulesMapper.getIntegralRuleByRuleType(gainRuleType);
    }
}
