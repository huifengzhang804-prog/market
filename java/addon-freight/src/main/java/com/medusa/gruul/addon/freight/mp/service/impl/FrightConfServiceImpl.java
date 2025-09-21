package com.medusa.gruul.addon.freight.mp.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import com.medusa.gruul.addon.freight.mp.mapper.FrightConfMapper;
import com.medusa.gruul.addon.freight.mp.service.IFrightConfService;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author xiaoq
 * @Description FrightConfServiceImpl.java
 * @date 2022-06-06 10:40
 */
@Service
public class FrightConfServiceImpl extends ServiceImpl<FrightConfMapper, FreightConf> implements IFrightConfService {
}
