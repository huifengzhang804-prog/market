package com.medusa.gruul.order.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.service.mp.mapper.ShopOrderMapper;
import com.medusa.gruul.order.service.mp.service.IShopOrderService;
import org.springframework.stereotype.Service;

/**
 * 店铺订单表 服务实现类
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Service
public class ShopOrderServiceImpl extends ServiceImpl<ShopOrderMapper, ShopOrder> implements
        IShopOrderService {

}
