package com.medusa.gruul.addon.integral.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.mp.entity.IntegralBehavior;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralBehaviorMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 积分行为数据实现层
 *
 * @author xiaoq
 * @Description IntegralBehaviorServiceImpl.java
 * @date 2023-02-06 15:40
 */
@Service
@RequiredArgsConstructor
public class IntegralBehaviorServiceImpl extends ServiceImpl<IntegralBehaviorMapper, IntegralBehavior> implements IIntegralBehaviorService {
}
