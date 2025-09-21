package com.medusa.gruul.user.service.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.IAddon;
import com.medusa.gruul.user.api.model.vo.IntegralRulesVO;

/**
 * @author shishuqian
 * date 2023/2/14
 * time 13:30
 **/

@AddonSupporter(id = "integralRule")
public interface IntegralAddonSupporter extends IAddon {


    /**
     * rpc获取积分商城积分规则
     *
     * @return 积分规则vo
     *
     * 插件实现服务 addon-integral {@link com.medusa.gruul.addon.integral.addon.impl.AddonIntegralProviderImpl#getIntegralRule}
     */
    @AddonMethod(returnType = IntegralRulesVO.class)
    IntegralRulesVO getIntegralRule();

}
