package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.mp.entity.IntegralConsumeOrder;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralConsumeOrderMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralConsumeOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/7/19
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class IntegralConsumeOrderServiceImpl extends ServiceImpl<IntegralConsumeOrderMapper, IntegralConsumeOrder> implements IIntegralConsumeOrderService {
}
