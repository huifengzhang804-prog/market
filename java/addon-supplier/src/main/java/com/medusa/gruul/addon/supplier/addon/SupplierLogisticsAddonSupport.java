package com.medusa.gruul.addon.supplier.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/7/24
 */
@AddonSupporter(service = Services.GRUUL_MALL_ORDER, id = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID)
public interface SupplierLogisticsAddonSupport {

    /**
     * 计算运费
     *
     * @param mode  配送方式
     * @param param 运费参数
     * @return 运费
     */
    @AddonMethod(returnType = Map.class, arg1Filter = true)
    Map<String, BigDecimal> distributionCost(DistributionMode mode, PlatformFreightParam param);

    /**
     * 打印并发货
     *
     * @param sendDeliveryDTO 发货DTO
     * @return 打印发货订单号
     */
    @AddonMethod(returnType = String.class)
    String printDeliverGoods(SendDeliveryDTO sendDeliveryDTO);


}
