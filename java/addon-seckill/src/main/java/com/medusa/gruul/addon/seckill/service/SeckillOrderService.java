package com.medusa.gruul.addon.seckill.service;

import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;

/**
 * 秒杀订单服务
 *
 * @author 张治保
 * @since 2024/6/5
 */
public interface SeckillOrderService {

    /**
     * 秒杀活动订单 支付
     *
     * @param orderPaidBroadcast 订单支付信息
     */
    void orderPaid(OrderPaidBroadcastDTO orderPaidBroadcast);
}
