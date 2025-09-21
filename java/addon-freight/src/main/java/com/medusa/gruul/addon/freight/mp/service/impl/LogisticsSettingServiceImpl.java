package com.medusa.gruul.addon.freight.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import com.medusa.gruul.addon.freight.mp.mapper.LogisticsSettingsMapper;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsSettingService;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author xiaoq
 * @Description LogisticsSettingServiceImpl.java
 * @date 2022-08-12 10:40
 */
@Service
public class LogisticsSettingServiceImpl extends ServiceImpl<LogisticsSettingsMapper, LogisticsSettings> implements ILogisticsSettingService {
}
