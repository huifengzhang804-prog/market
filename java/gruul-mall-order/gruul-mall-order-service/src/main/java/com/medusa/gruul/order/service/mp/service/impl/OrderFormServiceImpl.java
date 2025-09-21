package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.api.entity.OrderForm;
import com.medusa.gruul.order.service.mp.mapper.OrderFormMapper;
import com.medusa.gruul.order.service.mp.service.IOrderFormService;
import org.springframework.stereotype.Service;

/**
 * 订单表单 服务实现
 *
 * @author 张治保
 * @since 2022-05-19
 */
@Service
public class OrderFormServiceImpl extends ServiceImpl<OrderFormMapper, OrderForm> implements IOrderFormService {

}
