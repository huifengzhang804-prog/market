package com.medusa.gruul.addon.integral.addon;

import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-01-30 16:44
 */
public interface AddonIntegralProvider {


    /**
     * 积分规则 获取
     *
     * @return 积分规则VO
     */
    IntegralRulesVO getIntegralRule();


}
