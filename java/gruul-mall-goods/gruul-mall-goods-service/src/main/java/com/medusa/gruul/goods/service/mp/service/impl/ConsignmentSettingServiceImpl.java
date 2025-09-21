package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.ConsignmentSetting;
import com.medusa.gruul.goods.service.mp.mapper.ConsignmentSettingMapper;
import com.medusa.gruul.goods.service.mp.service.IConsignmentSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/8/8
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class ConsignmentSettingServiceImpl extends ServiceImpl<ConsignmentSettingMapper, ConsignmentSetting> implements IConsignmentSettingService {
}
