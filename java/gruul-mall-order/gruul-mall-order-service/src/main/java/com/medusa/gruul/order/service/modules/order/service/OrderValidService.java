package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.order.service.model.dto.OrderValidDTO;

/**
 * @author 张治保
 * @since 2024/4/2
 */
public interface OrderValidService {


    /**
     * 下单商品数据前置检查
     *
     * @param orderValid 前置检查参数
     */
    void orderValid(OrderValidDTO orderValid);
}
