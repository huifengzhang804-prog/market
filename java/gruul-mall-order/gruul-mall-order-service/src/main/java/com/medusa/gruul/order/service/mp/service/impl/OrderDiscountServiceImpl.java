package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.service.model.dto.OrderDetailQueryDTO;
import com.medusa.gruul.order.service.mp.mapper.OrderDiscountMapper;
import com.medusa.gruul.order.service.mp.service.IOrderDiscountService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 订单优惠项 服务实现类
 * 
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class OrderDiscountServiceImpl extends ServiceImpl<OrderDiscountMapper, OrderDiscount> implements IOrderDiscountService {

    @Override
    public List<OrderDiscount> orderDiscounts(OrderDetailQueryDTO query) {
        return baseMapper.orderDiscounts(query);
    }

    @Override
    public List<OrderDiscount> orderDiscountsByOrderNos(List<String> orderNos) {
        return baseMapper.orderDiscountsByOrderNos(orderNos);
    }


}
