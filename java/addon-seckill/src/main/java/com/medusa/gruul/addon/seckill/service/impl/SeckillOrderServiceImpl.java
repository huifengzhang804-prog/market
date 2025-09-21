package com.medusa.gruul.addon.seckill.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillOrderDao;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillOrder;
import com.medusa.gruul.addon.seckill.service.SeckillOrderService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/6/5
 */
@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl implements SeckillOrderService {

    private final SeckillOrderDao seckillOrderDao;

    @Override
    public void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast) {
        if (OrderType.SPIKE != orderPaidBroadcast.getActivityType()) {
            return;
        }
        String orderNo = orderPaidBroadcast.getOrderNo();
        Long activityId = orderPaidBroadcast.getActivityId();
        Long buyerId = orderPaidBroadcast.getBuyerId();
        List<OrderPaidBroadcastDTO.ShopPayAmountDTO> shopPayAmounts = orderPaidBroadcast.getShopPayAmounts();
        if (CollUtil.isEmpty(shopPayAmounts)) {
            return;
        }
        OrderPaidBroadcastDTO.ShopPayAmountDTO shopPayAmount = shopPayAmounts.get(CommonPool.NUMBER_ZERO);
        Long shopId = shopPayAmount.getShopId();
        seckillOrderDao.save(
                new SeckillOrder()
                        .setOrderNo(orderNo)
                        .setUserId(buyerId)
                        .setShopId(shopId)
                        .setActivityId(activityId)
        );
    }
}
