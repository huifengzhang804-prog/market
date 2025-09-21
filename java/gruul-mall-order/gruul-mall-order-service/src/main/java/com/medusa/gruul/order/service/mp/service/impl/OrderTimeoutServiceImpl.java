package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import com.medusa.gruul.order.service.mp.mapper.OrderTimeoutMapper;
import com.medusa.gruul.order.service.mp.service.IOrderTimeoutService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/2/8
 */
@Service
public class OrderTimeoutServiceImpl extends ServiceImpl<OrderTimeoutMapper, OrderTimeout> implements IOrderTimeoutService {
}
