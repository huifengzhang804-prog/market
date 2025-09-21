package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.model.dto.IntegralSettingDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralSetting;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralSettingMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author shishuqian
 * date 2023/2/9
 * time 11:13
 **/
@Service
@RequiredArgsConstructor
public class IntegralSettingServiceImpl extends ServiceImpl<IntegralSettingMapper, IntegralSetting> implements IIntegralSettingService {

    @Override
    public boolean add(IntegralSettingDTO dto) {
        IntegralSetting integralSetting = lambdaQuery().oneOpt()
                .orElse(new IntegralSetting())
                .setRatio(dto.getRatio())
                .setCeiling(dto.getCeiling());
        if (integralSetting.getId() == null) {
            return save(integralSetting);
        }
        return updateById(integralSetting);
    }


    @Override
    public IntegralSetting getSetting() {
        return this.lambdaQuery().one();
    }
}
