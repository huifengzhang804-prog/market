package com.medusa.gruul.order.service.modules.order.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import com.medusa.gruul.order.api.model.OrderDestination;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.api.model.ic.ICStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaoq
 * @Description 订单 物流supporter
 * @date 2023-05-08 18:52
 */
@AddonSupporter(id = OrderAddonConstant.ORDER_DISTRIBUTION_SUPPORT_ID)
public interface OrderAddonDistributionSupporter {


    /**
     * 计算快递运费
     *
     * @param mode         配送模式
     * @param freightParam 运费计算param
     * @return map<shop:物流模板id, cost>
     * <p>
     * 插件实现服务 *多元* addon-freight addon-intra-city-distribution
     */
    @AddonMethod(returnType = Map.class, arg1Filter = true)
    Map<String, BigDecimal> distributionCost(DistributionMode mode, PlatformFreightParam freightParam);


    /**
     * 打印并发货
     *
     * @param sendDeliveryDTO 发货DTO
     * @return 打印发货订单号
     * <p>
     * 插件实现服务  addon-freight {@link com.medusa.gruul.addon.freight.addon.impl.AddonFreightProviderImpl#printDeliverGoods}
     */
    @AddonMethod(returnType = String.class)
    String printDeliverGoods(SendDeliveryDTO sendDeliveryDTO);

    /**
     * 查询快递包裹是否签收
     * @param companyCode 快递公司
     * @param waybillNo 快递单号
     * @param recipientsPhone 收件人电话
     * 插件实现服务  addon-freight {@link com.medusa.gruul.addon.freight.addon.impl.AddonFreightProviderImpl#queryPackageSignedInfo}
     * @return
     */
    @AddonMethod(returnType = Boolean.class)
    Boolean queryPackageSignedInfo(String companyCode, String waybillNo, String recipientsPhone);

    /**
     * 同城配送单上报
     *
     * @param isSelf 是否时商家自配送
     * @param order  订单详情
     */
    void icOrderReport(Boolean isSelf, ICOrder order);

    /**
     * 确定所有的店铺是否都支持同城配送
     *com.medusa.gruul.addon.ic.addon.IcAddonProvider#checkAllSupportIc
     * @param shopIds
     * @return
     */
    boolean checkAllSupportIc(Set<Long> shopIds);

    /**
     * 批量查询同城订单状态
     *
     * @param orderNos              订单号
     * @param handledErrorToPending 已处理的错误标记为正常
     * @return 订单状态 map key 订单号 value 订单状态
     */
    Map<String, ICStatus> icOrderStatus(Set<String> orderNos, Boolean handledErrorToPending);


    /**
     * 获取 门店地址
     *
     * @param shopId  店铺 id
     * @param storeId 门店 id
     * @return 门店地址
     */
    OrderDestination storeAddress(Long shopId, Long storeId);


}
