package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderReceiver;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralOrderReceiverMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderReceiverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 15:21
 **/
@Service
@RequiredArgsConstructor
public class IntegralOrderReceiverServiceImpl extends ServiceImpl<IntegralOrderReceiverMapper, IntegralOrderReceiver> implements IIntegralOrderReceiverService {
}
