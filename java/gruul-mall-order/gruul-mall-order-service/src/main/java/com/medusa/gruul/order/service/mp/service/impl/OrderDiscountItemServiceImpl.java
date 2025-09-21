package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import com.medusa.gruul.order.service.mp.mapper.OrderDiscountItemMapper;
import com.medusa.gruul.order.service.mp.service.IOrderDiscountItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单优惠项 服务实现类
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class OrderDiscountItemServiceImpl extends ServiceImpl<OrderDiscountItemMapper, OrderDiscountItem> implements IOrderDiscountItemService {

    @Override
    public List<OrderDiscountItem> getItem(String orderNo, Long shopId, Long itemId) {
        return baseMapper.getItem(orderNo, shopId, itemId);
    }
}
