package com.medusa.gruul.addon.integral.rpc;

import com.medusa.gruul.addon.integral.mp.entity.IntegralSetting;

/**
 * @author shishuqian
 * date 2023/2/20
 * time 15:40
 **/
public interface IntegralOrderRpcService {

    /**
     * 获取积分抵扣设置
     * @return 积分抵扣设置
     */
    IntegralSetting getIntegralSetting();
}
