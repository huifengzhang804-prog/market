package com.medusa.gruul.addon.integral.service;

import com.medusa.gruul.addon.integral.model.dto.IntegralReceiverDTO;

/**
 * @author miskw
 * @date 2023/7/20
 * @describe 描述
 */
public interface ReceiverService {
    /**
     * 修改未支付订单收货地址
     *
     * @param orderNo 积分订单号
     * @param receiver 收货地址
     */
    void updateReceiver(String orderNo, IntegralReceiverDTO receiver);
}
