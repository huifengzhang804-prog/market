package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.service.model.dto.ReceiverChangeDTO;

/**
 * @author 张治保
 * date 2022/8/2
 */
public interface ReceiverService {

    /**
     * 修改订单收货人信息
     *
     * @param orderNo  订单号
     * @param receiver 收货人信息
     */
    void updateReceiver(String orderNo, ReceiverChangeDTO receiver);
}
