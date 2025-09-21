package com.medusa.gruul.addon.integral.mp.service.impl;

import com.medusa.gruul.addon.integral.mp.entity.IntegralSetting;
import com.medusa.gruul.addon.integral.mp.service.IIntegralSettingService;
import com.medusa.gruul.addon.integral.rpc.IntegralOrderRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author shishuqian
 * date 2023/2/20
 * time 15:42
 **/

@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class IntegralOrderRpcServiceImpl implements IntegralOrderRpcService {

    private final IIntegralSettingService iIntegralSettingService;

    @Override
    public IntegralSetting getIntegralSetting() {
        return this.iIntegralSettingService.getSetting();
    }
}
