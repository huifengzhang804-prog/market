package com.medusa.gruul.order.service.modules.order.service;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;

import java.util.Map;

/**
 * 订单商品运费计算服务层
 *
 * @author xiaoq
 * @Description OrderProductFreightCalculate
 * @date 2023-05-10 17:07
 */
public interface OrderProductFreightCalculate {
    /**
     *
     *
     * @param platformFreight 运费计算信息
     * @return <>
     */
    Map<DistributionMode, Map<String,?>> getFreightAmount(PlatformFreightParam platformFreight);
}
