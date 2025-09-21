package com.medusa.gruul.order.service.modules.orderTest.mock.rpc;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import com.medusa.gruul.order.api.model.OrderDestination;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/2/20
 */
public class AddonMockTest implements OrderAddonDistributionSupporter {
    @Override
    public Map<String, BigDecimal> distributionCost(DistributionMode mode, PlatformFreightParam freightParam) {
        return Map.of();
    }

    @Override
    public String printDeliverGoods(SendDeliveryDTO sendDeliveryDTO) {
        return "null";
    }

    @Override
    public Boolean queryPackageSignedInfo(String companyCode, String waybillNo, String recipientsPhone) {
        return null;
    }

    @Override
    public void icOrderReport(Boolean isSelf, ICOrder order) {

    }

    @Override
    public boolean checkAllSupportIc(Set<Long> shopIds) {
        return false;
    }

    @Override
    public Map<String, ICStatus> icOrderStatus(Set<String> orderNos, Boolean handledErrorToPending) {
        return Map.of();
    }


    @Override
    public OrderDestination storeAddress(Long shopId, Long storeId) {
        return null;
    }
}
