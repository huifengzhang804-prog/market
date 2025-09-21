package com.medusa.gruul.addon.live.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import com.medusa.gruul.addon.live.mp.mapper.ProhibitedMapper;
import com.medusa.gruul.addon.live.mp.service.ProhibitedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class ProhibitedServiceImpl extends ServiceImpl<ProhibitedMapper, Prohibited> implements ProhibitedService {
    @Override
    public Prohibited banReason(Long sourceId, ProhibitedType type) {
        return baseMapper.banReason(sourceId, type);
    }
}
