package com.medusa.gruul.addon.live.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 描述
 */
public interface ProhibitedService extends IService<Prohibited> {

    Prohibited banReason(Long sourceId, ProhibitedType type);
}
