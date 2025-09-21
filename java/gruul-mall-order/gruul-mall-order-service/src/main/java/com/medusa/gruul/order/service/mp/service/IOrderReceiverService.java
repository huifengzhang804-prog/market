package com.medusa.gruul.order.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderReceiver;

/**
 *
 * 订单优列表 服务类
 * 
 *
 * @author 张治保
 * @since 2022-07-19
 */
public interface IOrderReceiverService extends IService<OrderReceiver> {

    /**
     * 查询当前可用的收货人详情
     * @param orderNo 订单号
     * @param shopOrderNo 店铺id
     * @return 收货人详情
     */
    OrderReceiver getCurrentOrderReceiver(String orderNo, String shopOrderNo);
}
