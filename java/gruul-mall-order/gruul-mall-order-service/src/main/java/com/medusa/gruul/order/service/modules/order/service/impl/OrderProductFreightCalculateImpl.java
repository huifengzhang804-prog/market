package com.medusa.gruul.order.service.modules.order.service.impl;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.order.service.OrderProductFreightCalculate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单商品运费计算服务实现层
 *
 * @author xiaoq
 * Description OrderProductFreightCalculateImpl.java
 * date 2023-05-10 17:08
 */
@Service
@RequiredArgsConstructor
public class OrderProductFreightCalculateImpl implements OrderProductFreightCalculate {
    private final OrderAddonDistributionSupporter orderAddonDistributionSupporter;

    @Override
    public Map<DistributionMode, Map<String, ?>> getFreightAmount(PlatformFreightParam platformFreight) {
        Map<DistributionMode, Map<String, ?>> result = new HashMap<>(CommonPool.NUMBER_FIVE);
        List<DistributionMode> distributionMode = platformFreight.getDistributionMode();
        platformFreight.setDistributionMode(null);
        distributionMode.forEach(currentMode -> result.put(currentMode, currentModeFreightMap(currentMode, platformFreight)));
        return result;
    }

    private Map<String, ?> currentModeFreightMap(DistributionMode mode, PlatformFreightParam param) {
        try {
            return mode.getFunction()
                    .apply(innerMode -> orderAddonDistributionSupporter.distributionCost(innerMode, param));
        } catch (GlobalException exception) {
            return Map.of("code", exception.code());
        }
    }
}
