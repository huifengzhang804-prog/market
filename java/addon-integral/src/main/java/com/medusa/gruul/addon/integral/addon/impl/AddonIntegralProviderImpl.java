package com.medusa.gruul.addon.integral.addon.impl;

import com.medusa.gruul.addon.integral.addon.AddonIntegralProvider;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.service.ManageIntegralRuleService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.global.model.constant.Services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-01-30 16:44
 */


@Slf4j
@AddonProviders
@DubboService
@Service
@RequiredArgsConstructor
public class AddonIntegralProviderImpl implements AddonIntegralProvider {

    private final ManageIntegralRuleService manageIntegralRuleService;

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_USER, supporterId = "integralRule", methodName = "getIntegralRule", order = 1)
    public IntegralRulesVO getIntegralRule() {
        return this.manageIntegralRuleService.getIntegralRule();
    }
}
