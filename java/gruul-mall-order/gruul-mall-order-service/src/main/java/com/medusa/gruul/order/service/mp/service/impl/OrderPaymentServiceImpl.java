package com.medusa.gruul.order.service.mp.service.impl;

import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.service.mp.mapper.OrderPaymentMapper;
import com.medusa.gruul.order.service.mp.service.IOrderPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 订单支付信息 服务实现类
 * 
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements IOrderPaymentService {

}
